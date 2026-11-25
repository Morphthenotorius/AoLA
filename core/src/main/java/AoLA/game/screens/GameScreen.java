package AoLA.game.screens;

import AoLA.game.Main;
import AoLA.game.entities.Concrete.Enemy;
import AoLA.game.entities.Concrete.Player;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private final Main game;
    private Player player;
    private List<Enemy> enemies;
    private ShapeRenderer renderer;
    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        player = new Player(100,100);
        enemies = new ArrayList<>();
        enemies.add(new Enemy(300,300,500,300,100f,100,15,"skeleton",player));
        renderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        player.update(delta);
        for(Enemy enemy : enemies){
            enemy.update(delta);
        }
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(0.25f,0.25f,0.3f,1f);
        for(int x = 0; x < 1280; x+=64){
            renderer.line(x,0,x,720);
        }

        for (int y = 0; y < 720; y+= 64){
            renderer.line(0,y,1280,y);
        }
        renderer.end();


        game.batch.begin();
        player.render(game.batch);
        for(Enemy enemy : enemies){
            enemy.render(game.batch);
        }
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
        for(Enemy enemy : enemies){
            enemy.dispose();
        }

        renderer.dispose();
    }
}
