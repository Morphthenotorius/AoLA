package AoLA.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
public class MenuButton {
    private static final Color TEXT_COLOR = new Color(0.88f, 0.80f, 0.78f, 1f);
    private String text;
    private final Color color;
    private final Rectangle bounds = new Rectangle();
    private NinePatch normalPatch;
    private NinePatch hoverPatch;
    public MenuButton(String text, Color color) {
        this.text = text;
        this.color = color;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setSkin(NinePatch normal, NinePatch hover) {
        this.normalPatch = normal;
        this.hoverPatch = hover;
    }
    public static void applySkin(MenuButton[] buttons, NinePatch normal, NinePatch hover) {
        for (MenuButton button : buttons) {
            button.setSkin(normal, hover);
        }
    }

    public void setBounds(float centerX, float centerY, float width, float height) {
        bounds.set(centerX - width / 2f, centerY - height / 2f, width, height);
    }
    public boolean isHovered() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        return bounds.contains(mouseX, mouseY);
    }
    public boolean isClicked() {
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            return false;
        }
        return isHovered();
    }
    public void drawShape(ShapeRenderer shapeRenderer) {
        if (normalPatch != null) {
            return;
        }
        shapeRenderer.setColor(color);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    public void drawText(SpriteBatch batch, BitmapFont font, float scale) {
        if (normalPatch != null) {
            NinePatch patch = isHovered() ? hoverPatch : normalPatch;
            patch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        font.getData().setScale(scale*0.5f);
        font.setColor(TEXT_COLOR);
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(
            batch,
            text,
            bounds.x + (bounds.width - layout.width) / 2f,
            bounds.y + (bounds.height + layout.height) / 2f + 2f
        );
    }
    public static float scaleFor(float screenWidth, float screenHeight) {
        return Math.max(
            0.45f,
            Math.min(1f, Math.min(screenWidth / 900f, screenHeight / 600f))
        );
    }
    public static float layoutColumn(
        MenuButton[] buttons,
        float screenWidth,
        float screenHeight
    ) {
        float scale = scaleFor(screenWidth, screenHeight);
        float buttonWidth = Math.min(220f, screenWidth * 0.75f);
        float buttonHeight = Math.min(55f, screenHeight * 0.14f);
        float gap = 15f * scale;
        float totalHeight =
            buttons.length * buttonHeight + (buttons.length - 1) * gap;
        float top = screenHeight / 2f + totalHeight / 2f - 20f * scale;
        for (int i = 0; i < buttons.length; i++) {
            float centerY = top - buttonHeight / 2f - i * (buttonHeight + gap);
            buttons[i].setBounds(
                screenWidth / 2f, centerY, buttonWidth, buttonHeight
            );
        }
        return top;
    }
    public static void layoutBottom(
        MenuButton button,
        float screenWidth,
        float screenHeight
    ) {
        float buttonWidth = Math.min(220f, screenWidth * 0.75f);
        float buttonHeight = Math.min(55f, screenHeight * 0.14f);
        button.setBounds(
            screenWidth / 2f, buttonHeight * 1.2f, buttonWidth, buttonHeight
        );
    }
    public static void drawCentered(
        SpriteBatch batch,
        BitmapFont font,
        String text,
        float scale,
        float centerX,
        float topY
    ) {
        font.getData().setScale(scale);
        font.setColor(TEXT_COLOR);
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(batch, text, centerX - layout.width / 2f, topY);
    }
}
