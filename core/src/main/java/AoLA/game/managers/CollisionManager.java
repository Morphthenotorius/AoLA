
package AoLA.game.managers;

import AoLA.game.entities.Abstract.Living;
import AoLA.game.rules.GameRules;
import AoLA.game.world.MazeMap;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CollisionManager {
    private static final float CORNER_PADDING = 1f;
    private final MazeMap maze;
    private final GameRules gameRules;

    public CollisionManager(MazeMap maze, GameRules gameRules) {
        this.maze = maze;
        this.gameRules = gameRules;
    }

    //Moves a character and stops it before it enters a wall.
    public boolean move(Living character,float amountX, float amountY){
        Vector2 position = character.getPosition();
        float startX =position.x;
        float startY = position.y;

        // Check one axis at a time so the character can slide along a wall.
        if (canStandAt(character, position.x + amountX, position.y)) {
            position.x+= amountX;
        }
        if (canStandAt(character, position.x, position.y + amountY)) {
            position.y += amountY;
        }
        character.setPosition(position);
        return position.x != startX || position.y != startY;
    }

    /** Collects any key that overlaps the character. */
    public void collectKeys(Living character) {
        MapLayer keysLayer = maze.getMap().getLayers().get(MazeMap.KEYS_OBJECT_LAYER);
        Rectangle characterBounds = new Rectangle(
            character.getPosition().x,
            character.getPosition().y,
            character.getWidth(),
            character.getHeight()
        );

        for (MapObject key : keysLayer.getObjects()) {
            Integer keyId = key.getProperties().get("keyId", Integer.class);
            if (keyId == null || !key.isVisible() || !(key instanceof TextureMapObject)) {
                continue;
            }

            TextureMapObject tileKey = (TextureMapObject) key;
            float keyWidth = tileKey.getTextureRegion().getRegionWidth() * tileKey.getScaleX();
            float keyHeight = tileKey.getTextureRegion().getRegionHeight() * tileKey.getScaleY();
            Rectangle keyBounds = new Rectangle(tileKey.getX(), tileKey.getY(), keyWidth, keyHeight);

            if (characterBounds.overlaps(keyBounds) && gameRules.collectKey(keyId)) {
                key.setVisible(false);
                openUnlockedDoors();
            }
        }
    }

    /** Collects coins that overlap the character. */
    public int collectCoins(Living character) {
        MapLayer coinsLayer = maze.getMap().getLayers().get(MazeMap.KEYS_OBJECT_LAYER);
        Rectangle characterBounds = new Rectangle(
            character.getPosition().x,
            character.getPosition().y,
            character.getWidth(),
            character.getHeight()
        );

        int collectedCoins = 0;

        for (MapObject coin : coinsLayer.getObjects()) {
            if (!coin.isVisible() || !(coin instanceof TextureMapObject)
                || coin.getProperties().containsKey("keyId")) {
                continue;
            }

            TextureMapObject tileCoin = (TextureMapObject) coin;
            float coinWidth = tileCoin.getTextureRegion().getRegionWidth() * tileCoin.getScaleX();
            float coinHeight = tileCoin.getTextureRegion().getRegionHeight() * tileCoin.getScaleY();
            Rectangle coinBounds = new Rectangle(tileCoin.getX(), tileCoin.getY(), coinWidth, coinHeight);

            if (characterBounds.overlaps(coinBounds)) {
                coin.setVisible(false);
                collectedCoins++;
            }
        }

        return collectedCoins;
    }

    private void openUnlockedDoors() {
        MapLayer doorsLayer = maze.getMap().getLayers().get(MazeMap.DOORS_OBJECT_LAYER);

        for (MapObject door : doorsLayer.getObjects()) {
            Integer doorKeyId = door.getProperties().get("keyId", Integer.class);
            if (doorKeyId != null && gameRules.isDoorUnlocked(doorKeyId)
                && door instanceof RectangleMapObject) {
                Rectangle doorBounds = ((RectangleMapObject) door).getRectangle();
                maze.openDoor(doorBounds);
            }
        }
    }

    private boolean canStandAt(Living character, float x, float y) {
        int firstColumn = maze.worldToCol(x + CORNER_PADDING);
        int lastColumn = maze.worldToCol(x + character.getWidth() - CORNER_PADDING);
        int firstRow = maze.worldToRow(y + CORNER_PADDING);
        int lastRow = maze.worldToRow(y + character.getHeight() - CORNER_PADDING);

        // Check every tile touched by the character, including tiles between its corners.
        for (int column = firstColumn; column <= lastColumn; column++) {
            for (int row = firstRow; row <= lastRow; row++) {
                if (maze.isWallTile(column, row)) {
                    return false;
                }
            }
        }
        return !overlapsLockedDoor(character, x, y);
    }

    private boolean overlapsLockedDoor(Living character, float x, float y) {
        MapLayer doorsLayer = maze.getMap().getLayers().get(MazeMap.DOORS_OBJECT_LAYER);
        Rectangle characterBounds = new Rectangle(x, y, character.getWidth(), character.getHeight());

        for (MapObject door : doorsLayer.getObjects()) {
            Integer keyId = door.getProperties().get("keyId", Integer.class);
            if (keyId == null || gameRules.isDoorUnlocked(keyId)
                || !(door instanceof RectangleMapObject)) {
                continue;
            }

            Rectangle doorBounds = ((RectangleMapObject) door).getRectangle();
            if (characterBounds.overlaps(doorBounds)) {
                return true;
            }
        }
        return false;
    }
}
