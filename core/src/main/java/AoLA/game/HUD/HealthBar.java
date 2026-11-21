package AoLA.game.HUD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
public class HealthBar {
    private float health=100f;
    private float displayedHealth=100f;
    public void setHealth(float health) {
        this.health = MathUtils.clamp(health, 0f, 100f);
    }
    public void update(float delta) {
        displayedHealth=MathUtils.lerp(displayedHealth, health, Math.min(1f,5f * delta));
    }
    public float getHealth() { return health; }
    public float getDisplayedHealth() {
        return displayedHealth;
    }
    public Color getHealthColor() {
        if(health<=20f) {
            return Color.RED;
        }else if(health<=50f) {
            return Color.YELLOW;
        }else{
            return Color.GREEN;
        }}
    public void render(ShapeRenderer sr, float fx, float fy, float s) {
        float hx = fx + 30 * s, hy = fy + 9 * s, hw = 117 * s, hh = 15 * s;
        sr.setColor(Color.DARK_GRAY);
        sr.rect(hx, hy, hw, hh);
        float pct = displayedHealth / 100f;
        if (pct > 0.001f) {
            sr.setColor(getHealthColor());
            sr.rect(hx, hy, (9 + 108 * pct) * s, hh);
        }
    }
        }



