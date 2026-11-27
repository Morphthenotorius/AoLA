package AoLA.game.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class KeyIndicator {
    public static final float SCALE = 1.5f;
    public static final float FRAME_W = 151;
    public static final float FRAME_H = 37;
    private Texture fullHealthEmpty;
    private Texture fullHealthFull;
    private Texture middleHealthEmpty;
    private Texture middleHealthFull;
    private Texture lowHealthEmpty;
    private Texture lowHealthFull;
    private boolean hasKey = false;
    public KeyIndicator() {
        fullHealthEmpty = new Texture(Gdx.files.internal("health bars/full_health_key_empty.png"));
        fullHealthFull = new Texture(Gdx.files.internal("health bars/full_health_key_full.png"));
        middleHealthEmpty = new Texture(Gdx.files.internal("health bars/middle_health_key_empty.png"));
        middleHealthFull = new Texture(Gdx.files.internal("health bars/middle_health_key_full.png"));
        lowHealthEmpty = new Texture(Gdx.files.internal("health bars/low_health_key_empty.png"));
        lowHealthFull = new Texture(Gdx.files.internal("health bars/low_health_key_full.png"));
        fullHealthEmpty.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        fullHealthFull.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        middleHealthEmpty.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        middleHealthFull.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        lowHealthEmpty.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        lowHealthFull.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }
    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }
    public boolean hasKey() {
        return hasKey;
    }
    public void render(SpriteBatch batch, float x, float y, float health) {
        Texture texture;
        if (health > 50f) {
            texture = hasKey ? fullHealthFull : fullHealthEmpty;
        }
        else if (health > 20f) {
            texture = hasKey ? middleHealthFull : middleHealthEmpty;
        }
        else {
            texture = hasKey ? lowHealthFull : lowHealthEmpty;
        }
        batch.draw(texture, x, y, FRAME_W * SCALE, FRAME_H * SCALE);
    }
    public void dispose() {
        fullHealthEmpty.dispose();
        fullHealthFull.dispose();
        middleHealthEmpty.dispose();
        middleHealthFull.dispose();
        lowHealthEmpty.dispose();
        lowHealthFull.dispose();
    }
}
