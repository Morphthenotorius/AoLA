package AoLA.game.HUD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class HealthBar {
    private float health = 100f;
    private float displayedHealth = 100f;
    private final Texture pixel;
    public HealthBar() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();
    }
    public void setHealth(float health) {
        this.health = MathUtils.clamp(health, 0f, 100f);
    }
    public void update(float delta) {
        displayedHealth = MathUtils.lerp(displayedHealth, health, Math.min(1f, 5f * delta));
    }
    public float getHealth() { return health; }
    public float getDisplayedHealth() { return displayedHealth; }
    public Color getHealthColor() {
        if (health <= 20f) {
            return Color.RED;
        } else if (health <= 50f) {
            return Color.YELLOW;
        } else {
            return Color.GREEN;
        }
    }
    public void render(SpriteBatch batch, Rectangle bar) {
        batch.setColor(Color.DARK_GRAY);
        batch.draw(pixel, bar.x, bar.y, bar.width, bar.height);
        float pct = displayedHealth / 100f;
        if (pct > 0.001f) {
            batch.setColor(getHealthColor());
            float fillW = bar.width * (9f + 108f * pct) / 117f;
            batch.draw(pixel, bar.x, bar.y, fillW, bar.height);
        }
        batch.setColor(Color.WHITE);
    }
    public void dispose() {
        pixel.dispose();
    }
}
