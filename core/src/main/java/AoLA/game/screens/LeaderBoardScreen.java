package AoLA.game.screens;

import AoLA.game.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;

public class LeaderBoardScreen implements Screen {
    private final Main game;
    private final Screen previousScreen;
    private final MenuButton scoreBox =
        new MenuButton("Score: 0", new Color(0.30f, 0.30f, 0.34f, 1f));
    private final MenuButton backButton =
        new MenuButton("Back", new Color(0.15f, 0.30f, 0.60f, 1f));
    public LeaderBoardScreen(Main game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        backButton.setSkin(game.buttonNormal, game.buttonHover);
        scoreBox.setSkin(game.buttonNormal, game.buttonNormal);
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
        float panelWidth = Math.min(380f, width * 0.85f);
        float panelHeight = Math.min(430f, height * 0.85f);
        float panelX = (width - panelWidth) / 2f;
        float panelY = (height - panelHeight) / 2f;
        float buttonWidth = Math.min(220f, width * 0.75f);
        float buttonHeight = Math.min(55f, height * 0.14f);
        int trophyScale = Math.max(1, (int) (buttonHeight / game.trophyTexture.getHeight()));
        float trophyW = game.trophyTexture.getWidth() * trophyScale;
        float trophyH = game.trophyTexture.getHeight() * trophyScale;
        float gap = 12f * scale;
        float groupWidth = trophyW + gap + buttonWidth;
        float groupX = (width - groupWidth) / 2f;
        float scoreY = panelY + panelHeight * 0.5f;
        scoreBox.setBounds(groupX + trophyW + gap + buttonWidth / 2f, scoreY, buttonWidth, buttonHeight);
        scoreBox.setText("Score: " + game.getBestScore());
        backButton.setBounds(width / 2f, panelY + buttonHeight * 1.7f, buttonWidth, buttonHeight);
        if (backButton.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(previousScreen);
            return;
        }
        game.menuFont.getData().setScale(1f);
        float titleWidthAtOne = new GlyphLayout(game.menuFont, "LEADERBOARD").width;
        float titleScale = Math.min(2f * scale, panelWidth * 0.75f / titleWidthAtOne);
        ScreenUtils.clear(0.025f, 0.03f, 0.045f, 1f);
        game.batch.begin();
        game.batch.draw(game.uiPanel, panelX, panelY, panelWidth, panelHeight);
        MenuButton.drawCentered(game.batch, game.menuFont, "LEADERBOARD", titleScale, width / 2f, panelY + panelHeight - 90f * scale);
        game.batch.draw(game.trophyTexture, groupX, scoreY - trophyH / 2f, trophyW, trophyH);
        scoreBox.drawText(game.batch, game.menuFont, scale);
        backButton.drawText(game.batch, game.menuFont, scale);
        game.batch.end();
    }
    @Override
    public void resize(int width, int height) {
        game.resizeCamera(width, height);
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
