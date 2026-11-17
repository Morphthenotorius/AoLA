package AoLA.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimationLoader {
    // Written by Akbar
    private final Array<Texture> enemyTextures = new Array<>();
    private final Array<Texture> akberTextures = new Array<>();
    // Zombie
    private Animation<Texture> zombieDown;
    private Animation<Texture> zombieUp;
    private Animation<Texture> zombieRight;
    private Animation<Texture> zombieLeft;
    // Skeleton
    private Animation<Texture> skeletonDown;
    private Animation<Texture> skeletonUp;
    private Animation<Texture> skeletonLeft;
    private Animation<Texture> skeletonRight;
    // Mummy
    private Animation<Texture> mummyDown;
    private Animation<Texture> mummyUp;
    private Animation<Texture> mummyLeft;
    private Animation<Texture> mummyRight;
    // Akber
    private Animation<Texture> akberDown;
    private Animation<Texture> akberUp;
    private Animation<Texture> akberLeft;
    private Animation<Texture> akberRight;
    // Loads all animations
    public void load() {
        loadAkberAnimations();
        loadEnemyAnimations();
    }
    // Loads enemy animation frames
    private Animation<Texture> loadEnemyAnimation(String enemy, String direction) {
        Texture[] frames = new Texture[4];
        for (int i = 1; i <= 4; i++) {
            String path = "enemy_asset/" + enemy + "_" + direction
                + "/" + enemy + "_" + direction + "_" + i + ".png";
            Texture texture = new Texture(Gdx.files.internal(path));
            frames[i - 1] = texture;
            enemyTextures.add(texture);
        }
        Animation<Texture> animation = new Animation<>(0.15f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }
    // Loads Akber animation frames
    private Animation<Texture> loadAkberAnimation(String direction) {
        Texture[] frames = new Texture[4];
        for (int i = 1; i <= 4; i++) {
            String path = "akber.asset/akber." + direction
                + "/akber_" + direction + "." + i + ".png";
            Texture texture = new Texture(Gdx.files.internal(path));
            frames[i - 1] = texture;
            akberTextures.add(texture);
        }
        Animation<Texture> animation = new Animation<>(0.15f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }
    // Loads all enemy animations
    private void loadEnemyAnimations() {
        // Mummy
        mummyDown = loadEnemyAnimation("mummy", "down");
        mummyUp = loadEnemyAnimation("mummy", "up");
        mummyLeft = loadEnemyAnimation("mummy", "left");
        mummyRight = loadEnemyAnimation("mummy", "right");
        // Skeleton
        skeletonDown = loadEnemyAnimation("skeleton", "down");
        skeletonUp = loadEnemyAnimation("skeleton", "up");
        skeletonLeft = loadEnemyAnimation("skeleton", "left");
        skeletonRight = loadEnemyAnimation("skeleton", "right");
        // Zombie
        zombieDown = loadEnemyAnimation("zombie", "down");
        zombieUp = loadEnemyAnimation("zombie", "up");
        zombieLeft = loadEnemyAnimation("zombie", "left");
        zombieRight = loadEnemyAnimation("zombie", "right");
    }
    // Loads all Akber animations
    private void loadAkberAnimations() {
        akberDown = loadAkberAnimation("down");
        akberUp = loadAkberAnimation("up");
        akberLeft = loadAkberAnimation("left");
        akberRight = loadAkberAnimation("right");
    }
    // Akber getters
    public Animation<Texture> getAkberDown() {
        return akberDown;
    }
    public Animation<Texture> getAkberUp() {
        return akberUp;
    }
    public Animation<Texture> getAkberLeft() {
        return akberLeft;
    }
    public Animation<Texture> getAkberRight() {
        return akberRight;
    }
    // Zombie getters
    public Animation<Texture> getZombieDown() {
        return zombieDown;
    }
    public Animation<Texture> getZombieUp() {
        return zombieUp;
    }
    public Animation<Texture> getZombieLeft() {
        return zombieLeft;
    }
    public Animation<Texture> getZombieRight() {
        return zombieRight;
    }
    // Skeleton getters
    public Animation<Texture> getSkeletonDown() {
        return skeletonDown;
    }
    public Animation<Texture> getSkeletonUp() {
        return skeletonUp;
    }
    public Animation<Texture> getSkeletonLeft() {
        return skeletonLeft;
    }
    public Animation<Texture> getSkeletonRight() {
        return skeletonRight;
    }
    // Mummy getters
    public Animation<Texture> getMummyDown() {
        return mummyDown;
    }
    public Animation<Texture> getMummyUp() {
        return mummyUp;
    }
    public Animation<Texture> getMummyLeft() {
        return mummyLeft;
    }
    public Animation<Texture> getMummyRight() {
        return mummyRight;
    }
    // Disposes all loaded textures
    public void dispose() {
        for (Texture texture : enemyTextures) {
            texture.dispose();
        }
        for (Texture texture : akberTextures) {
            texture.dispose();
        }
        enemyTextures.clear();
        akberTextures.clear();
    }
}
