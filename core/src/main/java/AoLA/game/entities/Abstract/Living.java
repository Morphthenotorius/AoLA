package AoLA.game.entities.Abstract;

import AoLA.game.Enums.Direction;
import AoLA.game.Enums.EnemyState;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Living {

    protected Vector2 position;
    protected float speed;
    protected int health;
    protected int maxHealth;
    protected float width;
    protected float height;
    protected Rectangle bounds;
    protected static final int frameCount = 4;
    protected Direction currentDirection = Direction.DOWN;
    protected boolean isMoving = false;
    protected float stateTime = 0f;
    protected Array<Texture> loadedTextures = new Array<>();

    protected Animation<TextureRegion> walkDown;
    protected Animation<TextureRegion> walkUp;
    protected Animation<TextureRegion> walkLeft;
    protected Animation<TextureRegion> walkRight;

    protected Living(Vector2 position, float speed,int maxHealth, float width, float height) {
        this.position = position;
        this.speed = speed;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(position.x,position.y,width,height);

    }

    protected void initAnimations(String downPath, String upPath, String leftPath, String rightPath, int frameCount, float frameDuration) {
        this.walkDown = loadAnimation(downPath, frameCount, frameDuration);
        this.walkUp = loadAnimation(upPath, frameCount, frameDuration);
        this.walkLeft = loadAnimation(leftPath, frameCount, frameDuration);
        this.walkRight = loadAnimation(rightPath, frameCount, frameDuration);
    }


    protected Animation<TextureRegion> loadAnimation(String folderPath, int frameCount, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        String cleanPath = folderPath.endsWith("/") ? folderPath.substring(0, folderPath.length() - 1) : folderPath;
        String prefix = cleanPath.substring(cleanPath.lastIndexOf('/') + 1).replace('.','_');
        for (int i = 0; i < frameCount; i++) {
            Texture tex = new Texture(cleanPath + "/" + prefix + "_" + i + ".png");
            loadedTextures.add(tex);
            frames.add(new TextureRegion(tex));
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }
    public abstract void update(float delta);
    public void render (SpriteBatch batch){
        batch.draw(getCurrentFrame(),position.x,position.y, width, height);
    }

    protected TextureRegion getCurrentFrame(){
        switch(currentDirection){
            case UP:
                return walkUp.getKeyFrame(stateTime,true);
            case LEFT:
                return walkLeft.getKeyFrame(stateTime,true);
            case RIGHT:
                return walkRight.getKeyFrame(stateTime,true);
            case DOWN:
            default:
                return walkDown.getKeyFrame(stateTime,true);
        }
    }
    public void takeDamage(int damage){
        this.health = Math.max(0,this.health - damage);
    }

    public boolean isAlive(){
        return health>0;
    }

    public void dispose(){
        for(Texture tex : loadedTextures){
            tex.dispose();
        }
    }

    protected void updateBounds(){
        if(bounds != null){
            bounds.setPosition(position.x,position.y);
        }
    }

    //Getter and Setters!
    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public Rectangle getBounds(){
        return bounds;
    }

    //Acts


    protected void updateFacingDirection(Vector2 direction) {
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            currentDirection = (direction.x > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentDirection = (direction.y > 0) ? Direction.UP : Direction.DOWN;
        }
    }

    protected void stopAnimation() {
        isMoving = false;
        stateTime = 0f;
    }

    protected void moveTowards(Vector2 destination, float moveSpeed, float delta, Living obstacle) {
        Vector2 direction = new Vector2(destination).sub(position).nor();

        float oldX = position.x;
        position.x += direction.x * moveSpeed * delta;
        updateBounds();
        if(obstacle != null && bounds.overlaps(obstacle.getBounds())){
            position.x = oldX;
            updateBounds();
        }

        float oldY = position.y;
        position.y += direction.y * moveSpeed * delta;
        updateBounds();
        if(obstacle != null && bounds.overlaps(obstacle.getBounds())){
            position.y = oldY;
            updateBounds();
        }

        updateFacingDirection(direction);
        isMoving = true;
        stateTime += delta;
    }
}
