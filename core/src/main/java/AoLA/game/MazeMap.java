package AoLA.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
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
 * Loads and wraps a Tiled (.tmx) maze map, providing wall collision queries
 * and entity spawn locations for the rest of the game.
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
    private final Array<Vector2> enemySpawns = new Array<>();
    private final Array<Vector2> keySpawns = new Array<>();
    private final Array<Vector2> trapSpawns = new Array<>();

    /**
     * Loads an arbitrary Tiled map file and extracts its wall layer and spawn objects.
     *
     * @param tmxPath relative path inside the assets folder (e.g., "maze.tmx")
     */
    public MazeMap(String tmxPath) {
        this.tiledMap = new TmxMapLoader().load(tmxPath);

        MapLayer layer = tiledMap.getLayers().get("walls");
        if (layer == null) {
            layer = tiledMap.getLayers().get("foreground");
        }
        if (!(layer instanceof TiledMapTileLayer)) {
            throw new IllegalStateException("Map '" + tmxPath + "' must have a Tile Layer named 'walls' or 'foreground'.");
        }
        this.wallLayer = (TiledMapTileLayer) layer;

        this.tileWidth = wallLayer.getTileWidth();
        this.tileHeight = wallLayer.getTileHeight();
        this.mapWidthInTiles = wallLayer.getWidth();
        this.mapHeightInTiles = wallLayer.getHeight();

        parseObjects();
    }

    /**
     * Reads all objects placed on the Tiled "object" (or "spawns") layer.
     */
    private void parseObjects() {
        MapLayer objectLayer = tiledMap.getLayers().get("object");
        if (objectLayer == null) {
            objectLayer = tiledMap.getLayers().get("spawns");
        }
        if (objectLayer == null) return;

        for (MapObject obj : objectLayer.getObjects()) {
            String type = getObjectType(obj);
            if (type == null) continue;

            float x = obj.getProperties().get("x", Float.class);
            float y = obj.getProperties().get("y", Float.class);

            switch (type.toLowerCase()) {
                case "player_spawn":
                case "player":
                    playerSpawn = new Vector2(x, y);
                    break;
                case "exit":
                    if (obj instanceof RectangleMapObject) {
                        exitZone = ((RectangleMapObject) obj).getRectangle();
                    } else {
                        exitZone = new Rectangle(x, y, tileWidth, tileHeight);
                    }
                    break;
                case "enemy":
                    enemySpawns.add(new Vector2(x, y));
                    break;
                case "key":
                    keySpawns.add(new Vector2(x, y));
                    break;
                case "trap":
                    trapSpawns.add(new Vector2(x, y));
                    break;
            }
        }
    }

    private String getObjectType(MapObject obj) {
        if (obj.getName() != null && !obj.getName().isEmpty()) {
            return obj.getName();
        }
        String propType = obj.getProperties().get("type", String.class);
        if (propType != null && !propType.isEmpty()) {
            return propType;
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
     * Checks whether a specific grid coordinate contains a solid wall.
     */
    public boolean isWallAtGrid(int col, int row) {
        if (col < 0 || row < 0 || col >= mapWidthInTiles || row >= mapHeightInTiles) {
            return true;
        }
        return wallLayer.getCell(col, row) != null;
    }

    /**
     * Checks whether a world pixel coordinate lies inside a solid wall tile.
     */
    public boolean isWallAtWorld(float worldX, float worldY) {
        int col = (int) (worldX / tileWidth);
        int row = (int) (worldY / tileHeight);
        return isWallAtGrid(col, row);
    }

    public TiledMap getTiledMap() { return tiledMap; }
    public Vector2 getPlayerSpawn() { return playerSpawn; }
    public Rectangle getExitZone() { return exitZone; }
    public Array<Vector2> getEnemySpawns() { return enemySpawns; }
    public Array<Vector2> getKeySpawns() { return keySpawns; }
    public Array<Vector2> getTrapSpawns() { return trapSpawns; }
    public int getTileWidth() { return tileWidth; }
    public int getTileHeight() { return tileHeight; }

    @Override
    public void dispose() {
        tiledMap.dispose();
    }
}
