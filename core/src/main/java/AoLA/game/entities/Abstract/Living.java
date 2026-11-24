package AoLA.game.entities.Abstract;

import AoLA.game.Enums.Direction;
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

    protected Living(Vector2 position, float speed,int maxHealth) {
        this.position = position;
        this.speed = speed;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.width = 64f;
        this.height = 64f;
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
        String path = folderPath.endsWith("/") ? folderPath : folderPath + "/";
        for (int i = 0; i < frameCount; i++) {
            Texture tex = new Texture(path + i + ".png");
            loadedTextures.add(tex);
            frames.add(new TextureRegion(tex));
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }
    public abstract void update(float delta);
    public void render (SpriteBatch batch){
        batch.draw(getCurrentFrame(), position.x, position.y, width, height);
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
        updateBounds();
    }

    public float getSpeed() {
        return speed;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
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
}
