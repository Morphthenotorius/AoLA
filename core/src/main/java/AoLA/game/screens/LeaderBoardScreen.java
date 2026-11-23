package AoLA.game.screens;
import AoLA.game.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import java.awt.*;

public class LeaderBoardScreen implements Screen {
    private final Main game;
    private final Screen previousScreen;
    private final MenuButton backButton=new MenuButton("Back",new Color(0.15f,0.30f,0.60f,1f));
    public LeaderBoardScreen(Main game, Screen previousScreen) {
        this.game=game;
        this.previousScreen=previousScreen;
    }
    @Override
    public void show() {
        game.resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
    }
    @Override
    public void render(float delta) {
        int width=Gdx.graphics.getWidth();
        int height=Gdx.graphics.getHeight();
        float scale=MenuButton.scaleFor(width,height);
        MenuButton.layoutBottom(backButton,width,height);
        if(backButton.isClicked()||Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            game.setScreen(previousScreen);
            return;
        }
        ScreenUtils.clear(0.025f,0.30f,0.045f,1f);
        float titleY = height * 0.55f;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        backButton.drawShape(game.shapeRenderer);
        game.shapeRenderer.end();
        game.batch.begin();
        MenuButton.drawCentered(game.batch, game.menuFont, "LEADERBOARD", 2f * scale, width / 2f, titleY);
        MenuButton.drawCentered(game.batch, game.menuFont, "Best Score: " + game.getBestScore(), 1.4f * scale, width / 2f, titleY - 80f * scale);
        backButton.drawText(game.batch,game.menuFont,scale);
        game.batch.end();
    }
    @Override
    public void resize(int width, int height) {
        game.resizeCamera(width,height);
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
