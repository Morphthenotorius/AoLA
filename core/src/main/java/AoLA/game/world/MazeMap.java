package AoLA.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MazeMap implements Disposable {
    public static final String MAP_PATH = "maps/maze.tmx";
    public static final String WALLS_LAYER = "Walls";
    public static final String DOORS_TILE_LAYER = "Doors";
    public static final String KEYS_OBJECT_LAYER = "triggersObjects";
    public static final String DOORS_OBJECT_LAYER = "doorsObjects";
    public static final String PLAYER_SPAWN_NAME = "PlayerSpawn";

    private final TiledMap map;
    private final TiledMapTileLayer wallsLayer;
    private final OrthogonalTiledMapRenderer renderer;

    private final int widthTiles;
    private final int heightTiles;
    private final int tileWidth;
    private final int tileHeight;
    private final boolean[][] walls;

    public MazeMap(Batch batch) {
        map = new TmxMapLoader().load(MAP_PATH);

        if (!(map.getLayers().get(WALLS_LAYER) instanceof TiledMapTileLayer)) {
            map.dispose();
            throw new GdxRuntimeException("Missing tile layer '" + WALLS_LAYER + "' in " + MAP_PATH);
        }
        wallsLayer = (TiledMapTileLayer) map.getLayers().get(WALLS_LAYER);

        widthTiles = wallsLayer.getWidth();
        heightTiles = wallsLayer.getHeight();
        tileWidth = (int) wallsLayer.getTileWidth();
        tileHeight = (int) wallsLayer.getTileHeight();

        walls = new boolean[widthTiles][heightTiles];
        buildWallGrid();
        renderer = new OrthogonalTiledMapRenderer(map, 1f, batch);
    }

    public Vector2 getPlayerSpawn() {
        for (MapLayer layer : map.getLayers()) {
            MapObject obj = layer.getObjects().get(PLAYER_SPAWN_NAME);
            if (obj != null) {
                Float x = obj.getProperties().get("x", Float.class);
                Float y = obj.getProperties().get("y", Float.class);
                if (x != null && y != null) {
                    return new Vector2(x, y);
                }
            }
        }
        // fallback: center of the first non-wall tile found
        for (int col = 0; col < widthTiles; col++) {
            for (int row = 0; row < heightTiles; row++) {
                if (!walls[col][row]) {
                    return new Vector2(col * tileWidth, row * tileHeight);
                }
            }
        }
        return new Vector2(tileWidth, tileHeight);
    }

    private void buildWallGrid() {
        for (int x = 0; x < widthTiles; x++) {
            for (int y = 0; y < heightTiles; y++) {
                TiledMapTileLayer.Cell cell = wallsLayer.getCell(x, y);
                walls[x][y] = cell != null
                    && cell.getTile() != null
                    && cell.getTile().getId() != 0;
            }
        }
    }

    @Override
    public void dispose() {
        renderer.dispose();   // does not dispose the shared batch
        map.dispose();
    }

    public boolean isWallTile(int col, int row) {
        if (col < 0 || row < 0 || col >= widthTiles || row >= heightTiles) return true; // out of bounds blocks
        return walls[col][row];
    }

    public boolean isWall(float x, float y) {
        return isWallTile(worldToCol(x), worldToRow(y));
    }

    public int worldToCol(float x) { return (int) Math.floor(x / tileWidth); }
    public int worldToRow(float y) { return (int) Math.floor(y / tileHeight); }

    public float tileToWorldX(int col) { return col * tileWidth; }
    public float tileToWorldY(int row) { return row * tileHeight; }

    public TiledMap getMap() { return map; }
    public OrthogonalTiledMapRenderer getRenderer() { return renderer; }
    public int getWidthTiles() { return widthTiles; }
    public int getHeightTiles() { return heightTiles; }
    public int getTileWidth() { return tileWidth; }
    public int getTileHeight() { return tileHeight; }
    public int getWidthPixels() { return widthTiles * tileWidth; }
    public int getHeightPixels() { return heightTiles * tileHeight; }

    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    /** Draws the tile-backed objects stored in the map's object layers. */
    public void renderObjects(Batch batch) {
        for (MapLayer layer : map.getLayers()) {
            if (!layer.isVisible()) {
                continue;
            }

            for (MapObject object : layer.getObjects()) {
                if (!object.isVisible() || PLAYER_SPAWN_NAME.equals(object.getName()) || !(object instanceof TiledMapTileMapObject)) {
                    continue;
                }

                TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
                TextureRegion region = tileObject.getTextureRegion();

                batch.draw(
                    region,
                    tileObject.getX(),
                    tileObject.getY(),
                    tileObject.getOriginX(),
                    tileObject.getOriginY(),
                    region.getRegionWidth(),
                    region.getRegionHeight(),
                    tileObject.getScaleX(),
                    tileObject.getScaleY(),
                    tileObject.getRotation()
                );
            }
        }
    }

    /** Removes the door tiles that overlap this door object. */
    public void openDoor(Rectangle doorBounds) {
        TiledMapTileLayer doorsLayer = (TiledMapTileLayer) map.getLayers().get(DOORS_TILE_LAYER);
        if (doorsLayer == null) {
            return;
        }

        int firstColumn = worldToCol(doorBounds.x);
        int lastColumn = worldToCol(doorBounds.x + doorBounds.width - 1f);
        int firstRow = worldToRow(doorBounds.y);
        int lastRow = worldToRow(doorBounds.y + doorBounds.height - 1f);

        for (int column = firstColumn; column <= lastColumn; column++) {
            for (int row = firstRow; row <= lastRow; row++) {
                doorsLayer.setCell(column, row, null);
            }
        }
    }
}
