package AoLA.game.screens;

import AoLA.game.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {
    private final Main game;
    private final int score;
    private final MenuButton playAgainButton =
        new MenuButton("Play Again", new Color(0.62f, 0.20f, 0.17f, 1f));
    private final MenuButton menuButton =
        new MenuButton("Main Menu", new Color(0.30f, 0.30f, 0.34f, 1f));
    private final MenuButton[] buttons = { playAgainButton, menuButton };
    public GameOverScreen(Main game, int score) {
        this.game = game;
        this.score = score;
        MenuButton.applySkin(buttons, game.buttonNormal, game.buttonHover);
    }
    @Override
    public void show() {
        game.resizeCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    @Override
    public void render(float delta) {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        float scale = MenuButton.scaleFor(width, height);
        float top = MenuButton.layoutColumn(buttons, width, height);
        if (playAgainButton.isClicked()) {
            game.setScreen(new GameScreen(game));
            return;
        }
        if (menuButton.isClicked()) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        ScreenUtils.clear(0.15f, 0.02f, 0.03f, 1f);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (MenuButton button : buttons) {
            button.drawShape(game.shapeRenderer);
        }
        game.shapeRenderer.end();
        game.batch.begin();
        float skullSize = 64f * scale;
        game.batch.draw(
            game.skullTexture,
            width / 2f - skullSize / 2f,
            top + 125f * scale,
            skullSize,
            skullSize
        );
        MenuButton.drawCentered(
            game.batch, game.menuFont, "GAME OVER", 2f * scale,
            width / 2f, top + 110f * scale
        );
        MenuButton.drawCentered(
            game.batch, game.menuFont, "Coins: " + score, scale,
            width / 2f, top + 45f * scale
        );
        for (MenuButton button : buttons) {
            button.drawText(game.batch, game.menuFont, scale);
        }
        game.batch.end();
    }
    @Override
    public void resize(int width, int height) {
        game.resizeCamera(width, height);
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
    }
}
