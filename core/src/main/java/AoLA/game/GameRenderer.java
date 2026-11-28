package AoLA.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Renders the Tiled maze map, floor traps, room doors, and entities,
 * while managing the scrolling camera and screen viewport.
 */
public class GameRenderer implements Disposable {
    private final MazeMap mazeMap;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final Texture trapClosedTex;
    private final Texture trapOpenTex;
    private final Texture doorTex;

    /**
     * Initializes the map renderer, camera, viewport, and object textures.
     *
     * @param mazeMap        the active MazeMap to render
     * @param viewportWidth  virtual world width visible on screen in pixels
     * @param viewportHeight virtual world height visible on screen in pixels
     */
    public GameRenderer(MazeMap mazeMap, float viewportWidth, float viewportHeight) {
        this.mazeMap = mazeMap;
        this.mapRenderer = new OrthogonalTiledMapRenderer(mazeMap.getTiledMap());

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(viewportWidth, viewportHeight, camera);
        this.camera.position.set(mazeMap.getPlayerSpawn().x, mazeMap.getPlayerSpawn().y, 0);
        this.camera.update();

        this.trapClosedTex = loadOrFallback("trap_closed.png", Color.DARK_GRAY);
        this.trapOpenTex = loadOrFallback("trap_open.png", Color.RED);
        this.doorTex = loadOrFallback("room_door.png", Color.BROWN);
    }

    /**
     * Loads a texture from assets/ if present, or generates a solid 16x16 fallback
     * so rendering works immediately during testing.
     *
     * @param fileName asset filename
     * @param fallback fallback color if the PNG does not exist yet
     * @return loaded or generated Texture
     */
    private Texture loadOrFallback(String fileName, Color fallback) {
        if (Gdx.files.internal(fileName).exists()) {
            return new Texture(fileName);
        }
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(fallback);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    /**
     * Centers the camera on a target world position and renders the Tiled map,
     * traps, and closed room doors.
     *
     * @param batch        shared SpriteBatch used to draw sprites
     * @param targetCenter world position the camera should follow
     */
    public void render(SpriteBatch batch, Vector2 targetCenter) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        camera.position.x = Math.round(targetCenter.x);
        camera.position.y = Math.round(targetCenter.y);
        camera.update();

        // 1. Render Tiled ground and wall layers
        mapRenderer.setView(camera);
        mapRenderer.render();

        // 2. Render dynamic traps and closed doors
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Trap trap : mazeMap.getTraps()) {
            Texture tex = trap.isOpen() ? trapOpenTex : trapClosedTex;
            batch.draw(tex,
                trap.getPosition().x,
                trap.getPosition().y,
                trap.getBounds().width,
                trap.getBounds().height);
        }

        for (RoomDoor door : mazeMap.getRoomDoors()) {
            if (!door.isOpen()) {
                batch.draw(doorTex,
                    door.getPosition().x,
                    door.getPosition().y,
                    door.getBounds().width,
                    door.getBounds().height);
            }
        }

        batch.end();
    }

    /**
     * Updates the viewport when the desktop window is resized.
     *
     * @param width  new window width in pixels
     * @param height new window height in pixels
     */
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /**
     * Returns the game's OrthographicCamera.
     *
     * @return active camera
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Disposes of map renderer and texture resources.
     */
    @Override
    public void dispose() {
        mapRenderer.dispose();
        trapClosedTex.dispose();
        trapOpenTex.dispose();
        doorTex.dispose();
    }
}
