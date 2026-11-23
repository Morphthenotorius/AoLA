package AoLA.game.HUD;
import AoLA.game.HUD.CoinCounter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
public class HUD {
    private HealthBar healthBar;
    private KeyIndicator keyIndicator;
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout glyph = new GlyphLayout();
    private ShapeRenderer shapeRenderer;

    private CoinCounter coinCounter;
    public HUD(){
        healthBar = new HealthBar();
        keyIndicator = new KeyIndicator();
        shapeRenderer = new ShapeRenderer();
        font.getData().setScale(0.85f);
        coinCounter = new CoinCounter();
    }
    public void update(float delta){
        healthBar.update(delta);
    }
    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        float s  = KeyIndicator.SCALE;
        float fx = 20;
        float fy = height - 20 - KeyIndicator.FRAME_H * s;

        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        healthBar.render(shapeRenderer, fx, fy, s);
        shapeRenderer.end();
        batch.begin();
        keyIndicator.render(batch, fx, fy);
        float coinWidth  = 32;
        float coinHeight = 32;
        float coinX = width  - 120;
        float coinY = fy + (KeyIndicator.FRAME_H * s - coinHeight) / 2f;
        coinCounter.render(batch, coinX, coinY, coinWidth, coinHeight);
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
        font.dispose();
    }
}
