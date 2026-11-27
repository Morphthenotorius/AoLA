package AoLA.game.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameTimer {
    private float elapsed = 0f;
    private boolean running = true;
    private final Texture icon;
    private final BitmapFont font;
    public GameTimer(BitmapFont font) {
        this.font = font;
        icon = new Texture(Gdx.files.internal("ui/SandWatch.png"), true);
        icon.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
    }
    public void update(float delta) {
        if (running) elapsed += delta;
    }
    public void start() { running = true; }
    public void stop() { running = false; }
    public void reset() { elapsed = 0f; }
    public float getElapsedSeconds() { return elapsed; }
    public String getFormattedTime() {
        return format(elapsed);
    }
    public static String format(float seconds) {
        if (seconds < 0f) return "--:--";
        int total = (int) seconds;
        return String.format("%02d:%02d", total / 60, total % 60);
    }
    public static float iconAspect() {
        return 149f / 281f;
    }
    public void render(SpriteBatch batch, float x, float y, float iconH, float textX) {
        float iconW = iconH * iconAspect();
        batch.draw(icon, x, y, iconW, iconH);

        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        font.getData().setScale(0.85f);
        font.draw(batch, getFormattedTime(), textX, y + iconH / 2f + font.getCapHeight() / 2f);
        font.getData().setScale(oldScaleX, oldScaleY);
    }
    public void dispose() {
        icon.dispose();
    }
}
