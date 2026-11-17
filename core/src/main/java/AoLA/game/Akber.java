package AoLA.game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
public class Akber {
    private float x;
    private float y;
    private float stateTime = 0;
    private String direction = "down";
    private Animation<Texture> downAnimation;
    private Animation<Texture> upAnimation;
    private Animation<Texture> leftAnimation;
    private Animation<Texture> rightAnimation;
    public Akber(AnimationLoader loader) {
        downAnimation = loader.getAkberDown();
        upAnimation = loader.getAkberUp();
        leftAnimation = loader.getAkberLeft();
        rightAnimation = loader.getAkberRight();
    }
    // Set position
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    // Set movement direction
    public void setDirection(String direction) {
        this.direction = direction;
    }
    // Update animation time
    public void update(float deltaTime) {
        stateTime += deltaTime;
    }
    // Draw Akber
    public void render(SpriteBatch batch) {
        Animation<Texture> currentAnimation;
        switch (direction) {
            case "up":
                currentAnimation = upAnimation;
                break;
            case "left":
                currentAnimation = leftAnimation;
                break;
            case "right":
                currentAnimation = rightAnimation;
                break;
            default:
                currentAnimation = downAnimation;
                break;
        }
        Texture currentFrame = currentAnimation.getKeyFrame(stateTime);
        batch.draw(currentFrame, x, y);
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
}
