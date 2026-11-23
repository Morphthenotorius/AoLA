package AoLA.game;

import AoLA.game.screens.MenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public static final float WORLD_W = 640;
    public static final float WORLD_H = 480;
    public SpriteBatch batch;
    public BitmapFont menuFont;
    public OrthographicCamera camera;
    public Viewport viewport;
    public ShapeRenderer shapeRenderer;
    public BitmapFont font;
    private Preferences preferences;
    private int bestScore;
    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        FreeTypeFontGenerator generator=new FreeTypeFontGenerator(Gdx.files.internal("fonts/menu_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        menuFont = generator.generateFont(parameter);
        generator.dispose();
        preferences=Gdx.app.getPreferences("AoLALeaderboard");
        preferences = Gdx.app.getPreferences("AoLALeaderboard");
        bestScore = preferences.getInteger("bestScore", 0);
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_W, WORLD_H, camera);
        setScreen(new MenuScreen(this));
    }
    public void resizeCamera(int width, int height){
        camera.setToOrtho(false, width, height);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }
    public int getBestScore(){
        return bestScore;
    }
    public void submitScore(int score){
        if(score > bestScore){
            bestScore = score;
            preferences.putInteger("bestScore", bestScore);
            preferences.flush();
        }
    }
    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullscreen();
        }
        super.render();
    }
    public void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode((int) WORLD_W, (int) WORLD_H);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }
    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
