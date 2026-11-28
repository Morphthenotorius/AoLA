package AoLA.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class DoorArrow {
    private static final float ARROW_WIDTH = 30f;
    private static final float RING_RADIUS = 52f;
    private static final float HIDE_DISTANCE = 80f;
    private final Texture texture;
    private final TextureRegion region;
    private final Vector2 target = new Vector2();
    private final Vector2 direction = new Vector2();
    private boolean hasTarget = false;
    private float time = 0f;
    public DoorArrow() {
        texture = new Texture(Gdx.files.internal("ui/arrow.png"));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        region = new TextureRegion(texture);
    }
    public void setTarget(float x, float y) {
        target.set(x, y);
        hasTarget = true;
    }
    public void clearTarget() {
        hasTarget = false;
    }
    public void update(float delta) {
        time += delta;
    }
    public void render(SpriteBatch batch, Vector2 playerPosition, float playerHalfSize) {
        if (!hasTarget) {
            return;
        }
        float centerX = playerPosition.x + playerHalfSize;
        float centerY = playerPosition.y + playerHalfSize;
        direction.set(target.x - centerX, target.y - centerY);
        if (direction.len() < HIDE_DISTANCE) {
            return;
        }
        direction.nor();
        float radius = RING_RADIUS + MathUtils.sin(time * 6f) * 3f;
        float arrowX = centerX + direction.x * radius;
        float arrowY = centerY + direction.y * radius;
        float arrowHeight = ARROW_WIDTH * region.getRegionHeight() / region.getRegionWidth();
        float rotation = MathUtils.atan2(direction.y, direction.x)
            * MathUtils.radiansToDegrees - 90f;
        batch.draw(region, arrowX - ARROW_WIDTH / 2f, arrowY - arrowHeight / 2f, ARROW_WIDTH / 2f, arrowHeight / 2f,
            ARROW_WIDTH, arrowHeight, 1f, 1f, rotation);
    }
    public void dispose() {
        texture.dispose();
    }
}
