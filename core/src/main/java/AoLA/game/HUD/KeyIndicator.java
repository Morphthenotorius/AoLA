
package AoLA.game.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class KeyIndicator {
    public static final float SCALE = 1.5f;
    public static final float FRAME_W = 151;
    public static final float FRAME_H = 37;
    private final Texture[][] icons = new Texture[3][4];
    private int keyCount = 0;
    public KeyIndicator() {
        icons[0][0] = load("full_health_key_empty1.jpg");
        icons[0][1] = load("full_health_key_empty2.jpg");
        icons[0][2] = load("full_health_key_empty3.jpg");
        icons[0][3] = load("full_health_key_empty21.jpg");
        icons[1][0] = load("full_health_key_empty01.jpg");
        icons[1][1] = load("full_health_key_empty02.jpg");
        icons[1][2] = load("full_health_key_empty03.jpg");
        icons[1][3] = load("full_health_key_empty22.jpg");
        icons[2][0] = load("full_health_key_empty11.jpg");
        icons[2][1] = load("full_health_key_empty12.jpg");
        icons[2][2] = load("full_health_key_empty13.jpg");
        icons[2][3] = load("full_health_key_empty23.jpg");
    }
    private Texture load(String fileName) {
        Texture texture = new Texture(Gdx.files.internal("health bars/" + fileName));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }
    public void setKeyCount(int keyCount) {
        this.keyCount = Math.max(0, Math.min(3, keyCount));
    }
    public int getKeyCount() {
        return keyCount;
    }
    public void render(SpriteBatch batch, float x, float y, float health) {
        int healthTier;
        if (health > 50f) {
            healthTier = 0;
        } else if (health > 20f) {
            healthTier = 1;
        } else {
            healthTier = 2;
        }
        Texture texture = icons[healthTier][keyCount];
        batch.draw(texture, x, y, FRAME_W * SCALE, FRAME_H * SCALE
        );
    }
    public void dispose() {
        for (Texture[] row : icons) {
            for (Texture texture : row) {
                texture.dispose();
            }
        }
    }
}
