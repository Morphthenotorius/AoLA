package AoLA.game.screens;

import AoLA.game.HUD.HUD;
import AoLA.game.Main;
import AoLA.game.entities.Concrete.Player;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;

public class GameScreen implements Screen {
    private final Main game;
    private Player player;
    private HUD hud;
    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        player = new Player(100,100);
        hud =new HUD();
    }

    @Override
    public void render(float delta) {
        player.update(delta);
        hud.update(delta);
        hud.getHealthBar().setHealth(player.getHealth());
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);
        game.batch.begin();
        player.render(game.batch);
        hud.render(game.batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();
    }


    @Override
    public void resize(int width, int height) {

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
        hud.dispose();
    }
}
