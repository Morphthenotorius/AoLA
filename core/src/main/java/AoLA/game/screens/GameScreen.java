package AoLA.game.screens;

import AoLA.game.Main;
import AoLA.game.entities.Concrete.Player;
import AoLA.game.world.MazeMap;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
    private final Main game;
    private Player player;
    private MazeMap maze;
    private OrthographicCamera camera;
    private Viewport viewport;
    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera=new OrthographicCamera();
        viewport=new FitViewport(640,360,camera);
        maze=new MazeMap(game.batch);
        Vector2 spawn=maze.getPlayerSpawn();
        player = new Player(spawn.x, spawn.y);
        camera.position.set(spawn.x, spawn.y,0);
    }

    @Override
    public void render(float delta) {
        player.update(delta);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);
        camera.update();
        maze.render(camera);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.render(game.batch);
        game.batch.end();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        player.dispose();
        maze.dispose();
    }
}
