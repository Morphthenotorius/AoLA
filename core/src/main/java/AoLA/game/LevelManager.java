package AoLA.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages loading, switching, and disposing arbitrary Tiled maze levels.
 */
public class LevelManager implements Disposable {
    private final Array<String> levelPaths;
    private int currentLevelIndex;
    private MazeMap currentMap;

    /**
     * Creates a level manager initialized with a list of Tiled (.tmx) file paths.
     *
     * @param tmxPaths ordered paths to .tmx map files inside assets/
     */
    public LevelManager(String... tmxPaths) {
        this.levelPaths = new Array<>(tmxPaths);
        this.currentLevelIndex = 0;
        if (levelPaths.size > 0) {
            loadLevel(0);
        }
    }

    /**
     * Loads an arbitrary .tmx map file by path and disposes any previously loaded map.
     *
     * @param tmxPath relative path inside assets/ (e.g., "maze.tmx")
     * @return the newly loaded MazeMap
     */
    public MazeMap loadCustomMap(String tmxPath) {
        if (currentMap != null) {
            currentMap.dispose();
        }
        currentMap = new MazeMap(tmxPath);
        return currentMap;
    }

    /**
     * Loads the level at the given index in the configured level list.
     *
     * @param index 0-based level index
     * @return the loaded MazeMap
     */
    public MazeMap loadLevel(int index) {
        if (index < 0 || index >= levelPaths.size) {
            throw new IndexOutOfBoundsException("Invalid level index: " + index);
        }
        currentLevelIndex = index;
        return loadCustomMap(levelPaths.get(index));
    }

    /**
     * Advances to the next level in the list if one exists.
     *
     * @return true if the next level was loaded, false if already on the final level
     */
    public boolean nextLevel() {
        if (currentLevelIndex + 1 < levelPaths.size) {
            loadLevel(currentLevelIndex + 1);
            return true;
        }
        return false;
    }

    /**
     * Returns the currently active MazeMap.
     *
     * @return active MazeMap instance
     */
    public MazeMap getCurrentMap() {
        return currentMap;
    }

    /**
     * Disposes the currently loaded map resources.
     */
    @Override
    public void dispose() {
        if (currentMap != null) {
            currentMap.dispose();
            currentMap = null;
        }
    }
}
