package AoLA.game.HUD;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CoinCounter {
    private int coins=0;
    private Texture coinTexture;
    private BitmapFont font;
    public CoinCounter(BitmapFont font){
        coinTexture = new  Texture(Gdx.files.internal("coin.png"));
        this.font = font;
    }
    public void addCoin(){
        coins++;
    }
    public int getCoins(){
        return coins;
    }
    public int getScore(){
        return coins;
    }
    public void render(SpriteBatch batch,float x,float y,float width,float height){
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        batch.draw(coinTexture,x,y,width,height);
        font.getData().setScale(0.85f);
        font.draw(batch,String.valueOf(coins),x+width+10,y+height/2f+font.getCapHeight()/2f);
        font.getData().setScale(oldScaleX, oldScaleY);
    }
    public void dispose(){
        coinTexture.dispose();
    }
}
