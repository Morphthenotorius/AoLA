package AoLA.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Main entry point that loads maze.tmx, clamps the camera to map bounds,
 * and controls the 4-directional animated Akber player character.
 */
public class Main extends Game {
    public SpriteBatch batch;
    private LevelManager levelManager;
    private MazeMap mazeMap;
    private GameRenderer gameRenderer;

    private Vector2 playerPos;
    private Rectangle playerBounds;
    private int playerHealth = 10;
    private boolean levelWon = false;

    private float mapPixelWidth;
    private float mapPixelHeight;

    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;
    private Animation<TextureRegion> currentAnim;
    private float stateTime = 0f;
    private boolean isMoving = false;
    private final Array<Texture> loadedTextures = new Array<>();

    /**
     * Initializes the level, renderer, map bounds, and animated Akber player.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        levelManager = new LevelManager("maze.tmx");
        mazeMap = levelManager.getCurrentMap();
        gameRenderer = new GameRenderer(mazeMap, 320f, 240f);

        TiledMapTileLayer baseLayer = (TiledMapTileLayer) mazeMap.getTiledMap().getLayers().get(0);
        mapPixelWidth = baseLayer.getWidth() * baseLayer.getTileWidth();
        mapPixelHeight = baseLayer.getHeight() * baseLayer.getTileHeight();

        playerPos = new Vector2(mazeMap.getPlayerSpawn());
        playerBounds = new Rectangle(playerPos.x + 2f, playerPos.y + 2f, 12f, 12f);

        walkDown = loadDirectionAnim("akber.asset/akber.down");
        walkUp = loadDirectionAnim("akber.asset/akber.up");
        walkLeft = loadDirectionAnim("akber.asset/akber.left");
        walkRight = loadDirectionAnim("akber.asset/akber.right");
        currentAnim = walkDown;
    }

    /**
     * Loads all PNG frames inside a directional folder sorted alphabetically,
     * falling back to a gold square if the folder path is missing.
     *
     * @param folderPath relative folder path inside assets/
     * @return looping Animation of TextureRegions for that direction
     */
    private Animation<TextureRegion> loadDirectionAnim(String folderPath) {
        Array<TextureRegion> frames = new Array<>();
        FileHandle dir = Gdx.files.internal(folderPath);

        if (dir.exists() && dir.isDirectory()) {
            FileHandle[] files = dir.list(".png");
            Arrays.sort(files, Comparator.comparing(FileHandle::name));
            for (FileHandle file : files) {
                Texture tex = new Texture(file);
                tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                loadedTextures.add(tex);
                frames.add(new TextureRegion(tex));
            }
        }

        if (frames.size == 0) {
            Pixmap pm = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pm.setColor(Color.GOLD);
            pm.fill();
            Texture fallback = new Texture(pm);
            pm.dispose();
            loadedTextures.add(fallback);
            frames.add(new TextureRegion(fallback));
        }

        return new Animation<>(0.12f, frames, Animation.PlayMode.LOOP);
    }

    /**
     * Updates player movement, clamps camera inside the maze, checks traps/exit, and renders.
     */
    @Override
    public void render() {
        super.render();
        float delta = Gdx.graphics.getDeltaTime();

        if (!levelWon && playerHealth > 0) {
            updatePlayer(delta);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            playerPos.set(mazeMap.getPlayerSpawn());
            playerBounds.setPosition(playerPos.x + 2f, playerPos.y + 2f);
            playerHealth = 10;
            levelWon = false;
            currentAnim = walkDown;
        }

        stateTime = isMoving ? stateTime + delta : 0f;

        for (Trap trap : mazeMap.getTraps()) {
            trap.update(delta);
            if (trap.canDamage() && playerBounds.overlaps(trap.getBounds())) {
                playerHealth--;
                trap.triggerCooldown(1.0f);
                Gdx.app.log("Trap", "Hit by open trap! HP left: " + playerHealth);
            }
        }

        if (!levelWon && mazeMap.getExitZone() != null && playerBounds.overlaps(mazeMap.getExitZone())) {
            levelWon = true;
            Gdx.app.log("Victory", "You reached the exit! Press R to play again.");
        }

        // Clamp camera focus point so no dark empty void shows outside the map edges
        OrthographicCamera cam = gameRenderer.getCamera();
        float halfW = cam.viewportWidth * 0.5f;
        float halfH = cam.viewportHeight * 0.5f;
        float clampedX = (mapPixelWidth > cam.viewportWidth)
            ? MathUtils.clamp(playerPos.x + 8f, halfW, mapPixelWidth - halfW)
            : mapPixelWidth * 0.5f;
        float clampedY = (mapPixelHeight > cam.viewportHeight)
            ? MathUtils.clamp(playerPos.y + 8f, halfH, mapPixelHeight - halfH)
            : mapPixelHeight * 0.5f;

        gameRenderer.render(batch, new Vector2(clampedX, clampedY));

        TextureRegion frame = currentAnim.getKeyFrame(stateTime, true);
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(frame, playerPos.x, playerPos.y, 16f, 16f);
        batch.end();
    }

    /**
     * Moves the player in 4 directions using WASD or Arrow keys and slides along solid walls.
     *
     * @param delta elapsed frame time in seconds
     */
    private void updatePlayer(float delta) {
        float speed = 90f * delta;
        float dx = 0f;
        float dy = 0f;
        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx -= speed;
            currentAnim = walkLeft;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx += speed;
            currentAnim = walkRight;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy += speed;
            currentAnim = walkUp;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy -= speed;
            currentAnim = walkDown;
            isMoving = true;
        }

        playerBounds.setPosition(playerPos.x + dx + 2f, playerPos.y + 2f);
        if (!collidesWithWall(playerBounds)) {
            playerPos.x += dx;
        }

        playerBounds.setPosition(playerPos.x + 2f, playerPos.y + dy + 2f);
        if (!collidesWithWall(playerBounds)) {
            playerPos.y += dy;
        }

        playerBounds.setPosition(playerPos.x + 2f, playerPos.y + 2f);
    }

    /**
     * Checks whether the given hitbox overlaps any nearby solid wall tile.
     *
     * @param bounds entity bounding rectangle
     * @return true if colliding with a wall
     */
    private boolean collidesWithWall(Rectangle bounds) {
        for (Rectangle wall : mazeMap.getWallRectanglesNear(bounds)) {
            if (bounds.overlaps(wall)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gameRenderer.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        for (Texture tex : loadedTextures) {
            tex.dispose();
        }
        gameRenderer.dispose();
        levelManager.dispose();
    }
}
