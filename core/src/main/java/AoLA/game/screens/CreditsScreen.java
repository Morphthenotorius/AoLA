package AoLA.game.screens;

import AoLA.game.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class CreditsScreen implements Screen {
    private static final String[] CREDITS_LINES = {
        "Game Design: [men]",
        "Programming: [men",
        "Art and Animation: [men]",
        "Sound: [men]"
    };
    private final Main game;
    private final Screen previousScreen;
    private final MenuButton backButton =
        new MenuButton("Back", new Color(0.15f, 0.30f, 0.60f, 1f));
    public CreditsScreen(Main game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
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
        MenuButton.layoutBottom(backButton, width, height);
        if (backButton.isClicked()
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(previousScreen);
            return;
        }
        ScreenUtils.clear(0.025f, 0.03f, 0.045f, 1f);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        backButton.drawShape(game.shapeRenderer);
        game.shapeRenderer.end();
        game.batch.begin();
        MenuButton.drawCentered(
            game.batch, game.font, "CREDITS", 2f * scale,
            width / 2f, height * 0.85f
        );
        // Hər sətir əvvəlkindən aşağıda çəkilir.
        float lineY = height * 0.65f;
        float lineSpacing = 32f * scale;
        for (String line : CREDITS_LINES) {
            MenuButton.drawCentered(
                game.batch, game.font, line, 1.2f * scale, width / 2f, lineY
            );
            lineY -= lineSpacing;
        }
        backButton.drawText(game.batch, game.font, scale);
        game.font.getData().setScale(1f);
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
