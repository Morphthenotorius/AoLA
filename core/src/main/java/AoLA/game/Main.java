package AoLA.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Main entry point used to load, render, and inspect maze.tmx.
 */
public class Main extends Game {
    public SpriteBatch batch;
    private MazeMap mazeMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        mazeMap = new MazeMap("maze.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(mazeMap.getTiledMap());

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(mazeMap.getPlayerSpawn().x, mazeMap.getPlayerSpawn().y, 0);
        camera.zoom = 0.5f;
        camera.update();

        Gdx.app.log("MazeMap", "Player spawn: " + mazeMap.getPlayerSpawn());
        Gdx.app.log("MazeMap", "Exit zone: " + mazeMap.getExitZone());
    }

    @Override
    public void render() {
        super.render();
        handleCameraInput(Gdx.graphics.getDeltaTime());

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    private void handleCameraInput(float delta) {
        float speed = 300f * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) camera.position.x -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) camera.position.x += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) camera.position.y += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) camera.position.y -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) camera.zoom = Math.max(0.2f, camera.zoom - delta);
        if (Gdx.input.isKeyPressed(Input.Keys.E)) camera.zoom = Math.min(3f, camera.zoom + delta);
    }

    @Override
    public void dispose() {
        batch.dispose();
        mapRenderer.dispose();
        mazeMap.dispose();
    }
}
