package AoLA.game.HUD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class HUD {
    private final HealthBar healthBar;
    private final KeyIndicator keyIndicator;
    private final BitmapFont font;
    private final GlyphLayout glyph = new GlyphLayout();
    private final CoinCounter coinCounter;
    private final GameTimer timer;
    private final Rectangle bar = new Rectangle();
    public HUD(BitmapFont font) {
        this.font = font;
        healthBar = new HealthBar();
        keyIndicator = new KeyIndicator();
        coinCounter = new CoinCounter(font);
        timer = new GameTimer(font);
    }
    public void update(float delta) {
        healthBar.update(delta);
        timer.update(delta);
    }
    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        float s = KeyIndicator.SCALE;
        float fx = 20;
        float fy = height - 20 - KeyIndicator.FRAME_H * s;
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        float health = healthBar.getHealth();
        keyIndicator.getBarRect(fx, fy, health, bar);
        healthBar.render(batch, bar);
        keyIndicator.render(batch, fx, fy, health);
        String text = Math.round(health) + "%";
        font.getData().setScale(oldScaleX * s, oldScaleY * s);
        glyph.setText(font, text);
        font.setColor(Color.WHITE);
        font.draw(batch, text,
            bar.x + (bar.width - glyph.width) / 2f,
            bar.y + (bar.height + glyph.height) / 2f);
        font.getData().setScale(oldScaleX, oldScaleY);
        float coinWidth = 32;
        float coinHeight = 32;
        font.getData().setScale(0.85f);
        glyph.setText(font, "00:00");
        float timeTextW = glyph.width;
        font.getData().setScale(oldScaleX, oldScaleY);
        float coinX = width - 20f - (coinWidth + 10f + timeTextW);
        float coinY = fy + (KeyIndicator.FRAME_H * s - coinHeight) / 2f;
        coinCounter.render(batch, coinX, coinY, coinWidth, coinHeight);
        float timerIconH = 40f;
        float timerIconW = timerIconH * GameTimer.iconAspect();
        float timerX = coinX + (coinWidth - timerIconW) / 2f;
        float timerY = coinY - 10f - timerIconH;
        timer.render(batch, timerX, timerY, timerIconH, coinX + coinWidth + 10f);
        font.getData().setScale(oldScaleX, oldScaleY);
    }
    public void addCoin() { coinCounter.addCoin(); }
    public void setKeyCount(int count) { keyIndicator.setKeyCount(count); }
    public int getScore() { return coinCounter.getScore(); }
    public GameTimer getTimer() { return timer; }
    public HealthBar getHealthBar() { return healthBar; }
    public KeyIndicator getKeyIndicator() { return keyIndicator; }
    public void dispose() {
        keyIndicator.dispose();
        healthBar.dispose();
        coinCounter.dispose();
        timer.dispose();
    }
}
