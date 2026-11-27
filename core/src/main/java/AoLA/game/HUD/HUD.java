package AoLA.game.HUD;
import AoLA.game.HUD.CoinCounter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class HUD {
    private HealthBar healthBar;
    private KeyIndicator keyIndicator;
    private final BitmapFont font;
    private final GlyphLayout glyph = new GlyphLayout();
    private ShapeRenderer shapeRenderer;

    private CoinCounter coinCounter;
    public HUD(BitmapFont font){
        this.font=font;
        healthBar = new HealthBar();
        keyIndicator = new KeyIndicator();
        shapeRenderer = new ShapeRenderer();
        coinCounter = new CoinCounter(font);
    }
    public void update(float delta){
        healthBar.update(delta);
    }

    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        float s  = KeyIndicator.SCALE;
        float fx = 20;
        float fy = height - 20 - KeyIndicator.FRAME_H * s;
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        healthBar.render(shapeRenderer, fx, fy, s);
        shapeRenderer.end();
        batch.begin();
        keyIndicator.render(batch, fx, fy, healthBar.getHealth());
        String text=Math.round(healthBar.getHealth())+"%";
        glyph.setText(font,text);
        float barX=fx+30*s;
        float barY=fy+9*s;
        float barW=117*s;
        float barH=15*s;
        font.setColor(Color.WHITE);
        font.draw(batch,text,barX+(barW-glyph.width)/2f,barY+(barH+glyph.height)/2f);
        float coinWidth  = 32;
        float coinHeight = 32;
        float coinX = width  - 120;
        float coinY = fy + (KeyIndicator.FRAME_H * s - coinHeight) / 2f;
        coinCounter.render(batch, coinX, coinY, coinWidth, coinHeight);
        font.getData().setScale(oldScaleX, oldScaleY);
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
