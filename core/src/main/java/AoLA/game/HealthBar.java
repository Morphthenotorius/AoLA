package AoLA.game;

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
        public void render(ShapeRenderer shape, float x,float y,float width,float height){
        shape.setColor(getHealthColor());
        float fillWidth=width* (displayedHealth/100f);
        shape.rect(x, y, fillWidth, height);
        }


}
