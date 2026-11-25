package AoLA.game.screens;

import AoLA.game.Main;
import AoLA.game.entities.Concrete.Player;
import AoLA.game.managers.CollisionManager;
import AoLA.game.world.MazeMap;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class GameScreen implements Screen {
    private final Main game;
    private Player player;
    private MazeMap maze;
    private CollisionManager collisionManager;
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
        collisionManager = new CollisionManager(maze);
        Vector2 spawn=maze.getPlayerSpawn();
        player = new Player(spawn.x, spawn.y, collisionManager);
        camera.position.set(spawn.x, spawn.y,0);
    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.F11)){
            if(Gdx.graphics.isFullscreen()){
                Gdx.graphics.setWindowedMode(1280, 720);
            }
            else{
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
        player.update(delta);
        viewport.apply();
        camera.position.set(
            player.getPosition().x + 32f,
            player.getPosition().y + 32f,
            0f
        );
        camera.update();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);
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
