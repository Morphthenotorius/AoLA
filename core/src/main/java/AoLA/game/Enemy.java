package AoLA.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
public class Enemy {
    // Enemy position
    private float x;
    private float y;
    // Animation time
    private float stateTime = 0;
    // Enemy type and direction
    private String enemyType;
    private String direction = "down";
    // Animations
    private Animation<Texture> downAnimation;
    private Animation<Texture> upAnimation;
    private Animation<Texture> leftAnimation;
    private Animation<Texture> rightAnimation;
    public Enemy(AnimationLoader loader, String enemyType) {
        this.enemyType = enemyType.toLowerCase();
        switch (this.enemyType) {
            case "zombie":
                downAnimation = loader.getZombieDown();
                upAnimation = loader.getZombieUp();
                leftAnimation = loader.getZombieLeft();
                rightAnimation = loader.getZombieRight();
                break;
            case "skeleton":
                downAnimation = loader.getSkeletonDown();
                upAnimation = loader.getSkeletonUp();
                leftAnimation = loader.getSkeletonLeft();
                rightAnimation = loader.getSkeletonRight();
                break;
            case "mummy":
                downAnimation = loader.getMummyDown();
                upAnimation = loader.getMummyUp();
                leftAnimation = loader.getMummyLeft();
                rightAnimation = loader.getMummyRight();
                break;
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        }
    }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void setDirection(String direction) {
        if (direction.equals("up")
            || direction.equals("down")
            || direction.equals("left")
            || direction.equals("right")) {
            this.direction = direction;
        }
    }
    public void update(float deltaTime) {
        stateTime += deltaTime;
    }
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
    public String getEnemyType() {
        return enemyType;
    }
}
