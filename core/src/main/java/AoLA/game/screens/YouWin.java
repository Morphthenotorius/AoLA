package AoLA.game.screens;

import AoLA.game.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class YouWin implements Screen {
    private final Main game;
    private final int score;
    private static final Color DARK_TEXT = new Color(0.25f, 0.05f, 0.07f, 1f);
    private final MenuButton playAgainButton =
        new MenuButton("Play Again", new Color(0.62f, 0.20f, 0.17f, 1f));
    private final MenuButton menuButton =
        new MenuButton("Main Menu", new Color(0.30f, 0.30f, 0.34f, 1f));
    private final MenuButton[] buttons = {playAgainButton, menuButton};
    public YouWin(Main game, int score) {
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
        ScreenUtils.clear(Color.WHITE);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (MenuButton button : buttons) {
            button.drawShape(game.shapeRenderer);
        }
        game.shapeRenderer.end();
        game.batch.begin();
        float wingsWidth = 240f * scale;
        float wingsHeight = wingsWidth * game.wingsTexture.getHeight() / game.wingsTexture.getWidth();
        game.batch.draw(game.wingsTexture,
            width / 2f - wingsWidth / 2f, top + 125f * scale, wingsWidth, wingsHeight);
        MenuButton.drawCentered(
            game.batch, game.menuFont, "You Won!", 2f * scale,
            width / 2f, top + 110f * scale, DARK_TEXT
        );
        MenuButton.drawCentered(
            game.batch, game.menuFont, "Coins: " + score, scale,
            width / 2f, top + 45f * scale, DARK_TEXT
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
