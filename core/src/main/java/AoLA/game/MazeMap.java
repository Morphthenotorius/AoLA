package AoLA.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Loads and wraps an arbitrary Tiled (.tmx) maze map, providing wall collision
 * queries, exit zone detection, and parsed entity/trap/door spawns.
 */
public class MazeMap implements Disposable {
    private final TiledMap tiledMap;
    private final TiledMapTileLayer wallLayer;
    private final int tileWidth;
    private final int tileHeight;
    private final int mapWidthInTiles;
    private final int mapHeightInTiles;

    private Vector2 playerSpawn = new Vector2(32, 32);
    private Rectangle exitZone;
    private boolean exitLocked = false;

    private final Array<Vector2> enemySpawns = new Array<>();
    private final Array<Vector2> keySpawns = new Array<>();
    private final Array<Trap> traps = new Array<>();
    private final Array<RoomDoor> roomDoors = new Array<>();

    /**
     * Loads a Tiled (.tmx) maze file from the assets directory and parses its layers.
     *
     * @param tmxPath relative path inside the assets folder (e.g., "maze.tmx")
     * @throws IllegalStateException if no tile layer named "walls" or "foreground" exists
     */
    public MazeMap(String tmxPath) {
        this.tiledMap = new TmxMapLoader().load(tmxPath);

        MapLayer layer = tiledMap.getLayers().get("walls");
        if (layer == null) {
            layer = tiledMap.getLayers().get("foreground");
        }
        if (!(layer instanceof TiledMapTileLayer)) {
            throw new IllegalStateException(
                "Map '" + tmxPath + "' must contain a Tile Layer named 'walls' or 'foreground'.");
        }
        this.wallLayer = (TiledMapTileLayer) layer;

        this.tileWidth = wallLayer.getTileWidth();
        this.tileHeight = wallLayer.getTileHeight();
        this.mapWidthInTiles = wallLayer.getWidth();
        this.mapHeightInTiles = wallLayer.getHeight();

        parseObjects();
    }

    /**
     * Parses all objects on the Tiled "object" (or "spawns") layer and populates
     * player spawn, exit zone, traps, room doors, enemies, and keys.
     */
    private void parseObjects() {
        MapLayer objectLayer = tiledMap.getLayers().get("object");
        if (objectLayer == null) {
            objectLayer = tiledMap.getLayers().get("spawns");
        }
        if (objectLayer == null) {
            return;
        }

        for (MapObject obj : objectLayer.getObjects()) {
            String type = resolveObjectType(obj);
            if (type == null) {
                continue;
            }

            MapProperties props = obj.getProperties();
            float x = props.get("x", 0f, Float.class);
            float y = props.get("y", 0f, Float.class);
            float w = props.get("width", (float) tileWidth, Float.class);
            float h = props.get("height", (float) tileHeight, Float.class);

            switch (type.toLowerCase()) {
                case "player_spawn":
                case "player":
                    playerSpawn = new Vector2(x, y);
                    break;

                case "exit":
                    if (obj instanceof RectangleMapObject) {
                        exitZone = ((RectangleMapObject) obj).getRectangle();
                    } else {
                        exitZone = new Rectangle(x, y, w, h);
                    }
                    exitLocked = getBoolProperty(obj, "locked", false);
                    break;

                case "trap":
                    int damage = getIntProperty(obj, "damage", 1);
                    traps.add(new Trap(x, y, w, h, damage, 1.5f, 1.0f));
                    break;

                case "room_door":
                case "door":
                    boolean locked = getBoolProperty(obj, "locked", false);
                    int doorId = getIntProperty(obj, "doorId", 0);
                    int keyId = getIntProperty(obj, "keyId", 0);
                    String doorName = obj.getName() != null ? obj.getName() : "room_door";
                    roomDoors.add(new RoomDoor(doorName, x, y, w, h, locked, doorId, keyId));
                    break;

                case "enemy":
                    enemySpawns.add(new Vector2(x, y));
                    break;

                case "key":
                    keySpawns.add(new Vector2(x, y));
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Resolves an object's logical type from its Class/type, Name, or Tileset Tile Class.
     *
     * @param obj the Tiled map object
     * @return resolved identifier string, or null if none is set
     */
    private String resolveObjectType(MapObject obj) {
        String propType = obj.getProperties().get("type", String.class);
        if (propType != null && !propType.isEmpty()) {
            return propType;
        }
        if (obj.getName() != null && !obj.getName().isEmpty()) {
            return obj.getName();
        }
        if (obj instanceof TiledMapTileMapObject) {
            TiledMapTile tile = ((TiledMapTileMapObject) obj).getTile();
            if (tile != null && tile.getProperties().containsKey("type")) {
                return tile.getProperties().get("type", String.class);
            }
        }
        return null;
    }

    /**
     * Reads an integer custom property from the map object or its underlying tileset tile.
     *
     * @param obj          the Tiled map object
     * @param key          property name
     * @param defaultValue fallback value if unset
     * @return parsed integer value
     */
    private int getIntProperty(MapObject obj, String key, int defaultValue) {
        if (obj.getProperties().containsKey(key)) {
            Object val = obj.getProperties().get(key);
            if (val instanceof Number) {
                return ((Number) val).intValue();
            }
        }
        if (obj instanceof TiledMapTileMapObject) {
            TiledMapTile tile = ((TiledMapTileMapObject) obj).getTile();
            if (tile != null && tile.getProperties().containsKey(key)) {
                Object val = tile.getProperties().get(key);
                if (val instanceof Number) {
                    return ((Number) val).intValue();
                }
            }
        }
        return defaultValue;
    }

    /**
     * Reads a boolean custom property from the map object or its underlying tileset tile.
     *
     * @param obj          the Tiled map object
     * @param key          property name
     * @param defaultValue fallback value if unset
     * @return parsed boolean value
     */
    private boolean getBoolProperty(MapObject obj, String key, boolean defaultValue) {
        if (obj.getProperties().containsKey(key)) {
            Object val = obj.getProperties().get(key);
            if (val instanceof Boolean) {
                return (Boolean) val;
            }
        }
        if (obj instanceof TiledMapTileMapObject) {
            TiledMapTile tile = ((TiledMapTileMapObject) obj).getTile();
            if (tile != null && tile.getProperties().containsKey(key)) {
                Object val = tile.getProperties().get(key);
                if (val instanceof Boolean) {
                    return (Boolean) val;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Checks whether a tile grid cell contains a solid wall.
     *
     * @param col column index (0-based from left)
     * @param row row index (0-based from bottom)
     * @return true if the cell has a wall tile or lies outside map bounds
     */
    public boolean isWallAtGrid(int col, int row) {
        if (col < 0 || row < 0 || col >= mapWidthInTiles || row >= mapHeightInTiles) {
            return true;
        }
        return wallLayer.getCell(col, row) != null;
    }

    /**
     * Checks whether a world pixel coordinate lies inside a solid wall tile.
     *
     * @param worldX world x-coordinate in pixels
     * @param worldY world y-coordinate in pixels
     * @return true if blocked by a wall
     */
    public boolean isWallAtWorld(float worldX, float worldY) {
        int col = (int) (worldX / tileWidth);
        int row = (int) (worldY / tileHeight);
        return isWallAtGrid(col, row);
    }

    /**
     * Collects solid wall rectangles overlapping or adjacent to an entity's bounding box.
     *
     * @param entityBounds entity collision rectangle in world pixels
     * @return array of nearby wall rectangles
     */
    public Array<Rectangle> getWallRectanglesNear(Rectangle entityBounds) {
        Array<Rectangle> walls = new Array<>();
        int startCol = Math.max(0, (int) (entityBounds.x / tileWidth) - 1);
        int endCol = Math.min(mapWidthInTiles - 1, (int) ((entityBounds.x + entityBounds.width) / tileWidth) + 1);
        int startRow = Math.max(0, (int) (entityBounds.y / tileHeight) - 1);
        int endRow = Math.min(mapHeightInTiles - 1, (int) ((entityBounds.y + entityBounds.height) / tileHeight) + 1);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (wallLayer.getCell(col, row) != null) {
                    walls.add(new Rectangle(col * tileWidth, row * tileHeight, tileWidth, tileHeight));
                }
            }
        }
        return walls;
    }

    /**
     * Returns the underlying libGDX TiledMap for rendering.
     *
     * @return loaded TiledMap
     */
    public TiledMap getTiledMap() {
        return tiledMap;
    }

    /**
     * Returns the player's starting spawn position in world pixels.
     *
     * @return player spawn coordinates
     */
    public Vector2 getPlayerSpawn() {
        return playerSpawn;
    }

    /**
     * Returns the collision rectangle of the final maze exit.
     *
     * @return exit zone rectangle
     */
    public Rectangle getExitZone() {
        return exitZone;
    }

    /**
     * Returns whether the final exit requires unlocking before completion.
     *
     * @return true if locked, false if open immediately
     */
    public boolean isExitLocked() {
        return exitLocked;
    }

    /**
     * Returns all parsed floor traps on the map.
     *
     * @return array of Trap instances
     */
    public Array<Trap> getTraps() {
        return traps;
    }

    /**
     * Returns all parsed inner room doors on the map.
     *
     * @return array of RoomDoor instances
     */
    public Array<RoomDoor> getRoomDoors() {
        return roomDoors;
    }

    /**
     * Returns all enemy spawn coordinates parsed from the map.
     *
     * @return array of enemy spawn points
     */
    public Array<Vector2> getEnemySpawns() {
        return enemySpawns;
    }

    /**
     * Returns all key spawn coordinates parsed from the map.
     *
     * @return array of key spawn points
     */
    public Array<Vector2> getKeySpawns() {
        return keySpawns;
    }

    /**
     * Returns the width of a single tile in pixels.
     *
     * @return tile width in pixels
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * Returns the height of a single tile in pixels.
     *
     * @return tile height in pixels
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * Releases native resources held by the TiledMap.
     */
    @Override
    public void dispose() {
        tiledMap.dispose();
    }
}
