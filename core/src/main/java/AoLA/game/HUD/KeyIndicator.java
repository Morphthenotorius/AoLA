package AoLA.game.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class KeyIndicator {
    public static final float SCALE = 1.5f;
    public static final float FRAME_W = 151;
    public static final float FRAME_H = 37;
    private Texture emptyTexture;
    private Texture fullTexture;
    private boolean hasKey = false;
    public KeyIndicator() {
        emptyTexture = new Texture(Gdx.files.internal("health bars/health_bar_key_empty_frame.png"));
        fullTexture  = new Texture(Gdx.files.internal("health bars/health_bar_key_full_frame.png"));
        emptyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        fullTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }
    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }
    public void render(SpriteBatch batch, float x, float y) {
        Texture t = hasKey ? fullTexture : emptyTexture;
        batch.draw(t, x, y, FRAME_W * SCALE, FRAME_H * SCALE);
    }
    public void dispose() {
        emptyTexture.dispose();
        fullTexture.dispose();
    }
}
