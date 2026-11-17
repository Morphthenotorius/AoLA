package AoLA.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    //written by Akbar
    private final Array<Texture> enemyTextures=new Array<>();
    private final Array<Texture> akberTextures=new Array<>();
    //Zombie
    private Animation<Texture> zombiedown;
    private Animation<Texture> zombieup;
    private Animation<Texture> zombieright;
    private Animation<Texture> zombieleft;
    //skeleton
    private Animation<Texture> skeletondown;
    private Animation<Texture> skeletonup;
    private Animation<Texture> skeletonleft;
    private Animation<Texture> skeletonright;
    //Mummy
    private Animation<Texture> mummydown;
    private Animation<Texture> mummyup;
    private Animation<Texture> mummyleft;
    private Animation<Texture> mummyright;
    private Animation<Texture> akberdown;
    private Animation<Texture> akberup;
    private Animation<Texture> akberleft;
    private Animation<Texture> akberright;
    private Animation<Texture> loadEnemyAnimation(
        String enemy,
        String direction){
        Texture[] frames=new Texture[4];
        for(int i=1;i<=4;i++){
            String path="enemy_asset/"+enemy+"_"+direction+"/"+enemy+"_"+direction+"_"+i+".png";
            Texture texture= new Texture(Gdx.files.internal(path));
            frames[i-1]=texture;
            enemyTextures.add(texture);
        }
        Animation<Texture> animation=new Animation<>(0.15f,frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }
    private Animation<Texture> loadAkberAnimation(String direction){
        Texture[] frames=new Texture[4];
        for(int i=1;i<=4;i++){
            String path="akber.asset/akber."+direction+"/akber_"+direction+"."+i+".png";
            Texture texture= new Texture(Gdx.files.internal(path));
            frames[i-1]=texture;
            akberTextures.add(texture);
        }
        Animation<Texture> animation=new Animation<>(0.15f,frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }
    private void loadEnemyAnimations(){
        //Mummy
        mummydown=loadEnemyAnimation("mummy","down");
        mummyup=loadEnemyAnimation("mummy","up");
        mummyleft=loadEnemyAnimation("mummy","left");
        mummyright=loadEnemyAnimation("mummy","right");
        //Skeleton
        skeletondown=loadEnemyAnimation("skeleton","down");
        skeletonup=loadEnemyAnimation("skeleton","up");
        skeletonleft=loadEnemyAnimation("skeleton","left");
        skeletonright=loadEnemyAnimation("skeleton","right");
        //Zombie
        zombiedown=loadEnemyAnimation("zombie","down");
        zombieup=loadEnemyAnimation("zombie","up");
        zombieleft=loadEnemyAnimation("zombie","left");
        zombieright=loadEnemyAnimation("zombie","right");
    }
    private void loadAkberAnimations(){
        //Akbers animations
        akberdown=loadAkberAnimation("down");
        akberup=loadAkberAnimation("up");
        akberleft=loadAkberAnimation("left");
        akberright=loadAkberAnimation("right");
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
