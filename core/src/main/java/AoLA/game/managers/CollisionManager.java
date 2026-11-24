package AoLA.game.managers;

import AoLA.game.entities.Abstract.Living;
import AoLA.game.world.MazeMap;
import com.badlogic.gdx.math.Vector2;

public class CollisionManager {
    private static final float CORNER_PADDING = 1f;
    private final MazeMap maze;
    public CollisionManager(MazeMap maze) {
        this.maze = maze;
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
        return true;
    }
}
