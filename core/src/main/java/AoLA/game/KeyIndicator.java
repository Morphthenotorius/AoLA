package AoLA.game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
public class KeyIndicator {
    private Texture emptyTexture;
    private Texture fullTexture;
    private boolean hasKey=false;
    public KeyIndicator(){
        emptyTexture=new Texture(Gdx.files.internal("health bars/health_bar_key_empty.png"));
        fullTexture=new Texture(Gdx.files.internal("health bars/health_bar_key_full.png"));
    }
    public void setHasKey(boolean hasKey){
        this.hasKey=hasKey;
    }
    public void render(SpriteBatch batch, float x,float y, float width,float height){
        Texture currentTexture=hasKey ? fullTexture : emptyTexture;
        batch.draw(currentTexture,x,y,width,height);
    }
        public void dispose(){
            emptyTexture.dispose();
            fullTexture.dispose();
        }

}
