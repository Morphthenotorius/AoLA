package AoLA.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
public class HUD {
    private HealthBar healthBar;
    private KeyIndicator keyIndicator;
    private ShapeRenderer shapeRenderer;
    private CoinCounter coinCounter;
    public HUD(){
        healthBar = new HealthBar();
        keyIndicator = new KeyIndicator();
        shapeRenderer = new ShapeRenderer();
        coinCounter = new CoinCounter();
    }
    public void update(float delta){
        healthBar.update(delta);
    }
    public void render(SpriteBatch batch,float x,float y,float width,float height){
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        healthBar.render(shapeRenderer,x+70,y+20,width-130,height-40);
        shapeRenderer.end();
        batch.begin();
        keyIndicator.render(batch,x,y,width,height);
        float coinWidth=32;
        float coinHeight=32;
        float coinX=Gdx.graphics.getWidth()-120;
        float coinY=Gdx.graphics.getHeight()-52;
        coinCounter.render(batch,coinX,coinY,coinWidth,coinHeight);
    }
    public void addCoin(){
        coinCounter.addCoin();
    }
    public int getScore(){
        return coinCounter.getScore();
    }
    public HealthBar getHealthBar() {
        return healthBar;
    }
    public KeyIndicator getKeyIndicator() {
        return keyIndicator;
    }
    public void dispose(){
        keyIndicator.dispose();
        shapeRenderer.dispose();
        coinCounter.dispose();
    }
}
