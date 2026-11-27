package AoLA.game.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class KeyIndicator {
    public static final float SCALE = 2f;
    public static final float FRAME_H = 35f;
    private static final float DRAW_W = 150f * SCALE;
    private static final float REF_SRC_W = 2240f;
    private static final float K = DRAW_W / REF_SRC_W;
    private static final float BAR_X = 32f * SCALE;
    private static final float BAR_Y = 8f * SCALE;

    private static final class Frame {
        Texture texture;
        int w, h;
        int bx0, by0, bx1, by1;
    }
    private final Frame[][] frames = new Frame[3][4];
    private int keyCount = 0;
    public KeyIndicator() {
        String[] names = {"high", "middle", "low"};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                String path = "health bars/" + names[i] + "/" + names[i]
                    + "_keyindicator_key" + j + ".png";
                frames[i][j] = loadFrame(path);
            }
        }
    }
    private static int alpha(Pixmap pm, int x, int y) {
        return pm.getPixel(x, y) & 0xFF; // RGBA8888
    }
    private static Frame loadFrame(String path) {
        Pixmap pm = new Pixmap(Gdx.files.internal(path));
        if (pm.getFormat() != Pixmap.Format.RGBA8888) {
            Pixmap c = new Pixmap(pm.getWidth(), pm.getHeight(), Pixmap.Format.RGBA8888);
            c.drawPixmap(pm, 0, 0);
            pm.dispose();
            pm = c;
        }
        int w = pm.getWidth(), h = pm.getHeight();
        int cx = w / 2;
        int bestS = 0, bestE = 0, s = -1;
        for (int y = 0; y <= h; y++) {
            boolean clear = y < h && alpha(pm, cx, y) <= 10;
            if (clear) {
                if (s < 0) s = y;
            } else if (s >= 0) {
                if (y - s > bestE - bestS) { bestS = s; bestE = y; }
                s = -1;
            }
        }
        if (bestE <= bestS) {
            pm.dispose();
            throw new GdxRuntimeException("Bar daxili sahəsi tapılmadı: " + path);
        }
        int ym = (bestS + bestE) / 2;
        int l = cx;
        while (l > 0 && alpha(pm, l - 1, ym) <= 10) l--;
        int r = cx;
        while (r < w - 1 && alpha(pm, r + 1, ym) <= 10) r++;
        r++;
        Frame f = new Frame();
        f.w = w;
        f.h = h;
        f.bx0 = l;
        f.bx1 = r;
        f.by0 = h - bestE;
        f.by1 = h - bestS;
        Texture t = new Texture(pm, true);
        pm.dispose();
        t.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        f.texture = t;
        return f;
    }
    public void setKeyCount(int count) {
        keyCount = Math.max(0, Math.min(3, count));
    }
    private static int tierFor(float health) {
        if (health > 50) return 0;
        if (health > 20) return 1;
        return 2;
    }
    public Rectangle getBarRect(float fx, float fy, float health, Rectangle out) {
        Frame f = frames[tierFor(health)][keyCount];
        return out.set(fx + BAR_X, fy + BAR_Y, (f.bx1 - f.bx0) * K, (f.by1 - f.by0) * K);
    }
    public void render(SpriteBatch batch, float fx, float fy, float health) {
        Frame f = frames[tierFor(health)][keyCount];
        float x = fx + BAR_X - f.bx0 * K;
        float y = fy + BAR_Y - f.by0 * K;
        batch.draw(f.texture, x, y, f.w * K, f.h * K);
    }
    public void dispose() {
        for (Frame[] row : frames) {
            for (Frame f : row) {
                f.texture.dispose();
            }
        }
    }
}
