package AoLA.game.HUD;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CoinCounter {
    private int coins=0;
    private Texture coinTexture;
    private BitmapFont font;
    public CoinCounter(){
        coinTexture = new  Texture(Gdx.files.internal("coin.png"));
        font = new BitmapFont();
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
        batch.draw(coinTexture,x,y,width,height);
        font.draw(batch,String.valueOf(coins),x+width+10,y+height/2f+6f);
    }
    public void dispose(){
        coinTexture.dispose();
        font.dispose();
    }
}
