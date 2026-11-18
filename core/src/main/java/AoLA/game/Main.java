package AoLA.game;

import AoLA.game.entities.Concrete.Player;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public SpriteBatch batch;
    private Player player;

    @Override
    public void create() {
        batch = new SpriteBatch();
        player = new Player(140, 210);
    }

    @Override
    public void render() {
        super.render();
        float delta = Gdx.graphics.getDeltaTime();
        player.update(delta);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        player.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
    }
}
