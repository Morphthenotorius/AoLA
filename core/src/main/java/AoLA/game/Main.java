package AoLA.game;
import AoLA.game.graphics.SoundManager;
import AoLA.game.screens.MenuScreen;
import AoLA.game.screens.GameScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public static final float WORLD_W = 640;
    public static final float WORLD_H = 480;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public Texture trophyTexture;
    public BitmapFont menuFont;
    public OrthographicCamera camera;
    public Viewport viewport;
    public NinePatch buttonNormal;
    public BitmapFont titleFont;
    public NinePatch buttonHover;
    public Texture skullTexture;
    public Texture wingsTexture;
    public Texture uiPanel;
    private Texture buttonNormalTexture;
    private Texture buttonHoverTexture;
    private Preferences preferences;
    private int bestScore;

    public SoundManager soundManager;
    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/menu_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.magFilter = Texture.TextureFilter.Nearest;
        menuFont = generator.generateFont(parameter);
        parameter.size = 64;
        titleFont = generator.generateFont(parameter);
        generator.dispose();
        skullTexture = new Texture(Gdx.files.internal("ui/skull.png"));
        wingsTexture = new Texture(Gdx.files.internal("ui/Wings.jpeg"));
        trophyTexture = new Texture(Gdx.files.internal("ui/trophy.png"));
        trophyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        wingsTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buttonNormalTexture = new Texture(Gdx.files.internal("ui/button_normal.png"));
        buttonHoverTexture = new Texture(Gdx.files.internal("ui/button_hover.png"));
        buttonNormalTexture.setFilter(
            Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        uiPanel = new Texture(Gdx.files.internal("ui_panel.png"));
        uiPanel.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonHoverTexture.setFilter(
            Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        int edge = 6;
        buttonNormal = new NinePatch(buttonNormalTexture, edge, edge, edge, edge);
        buttonHover = new NinePatch(buttonHoverTexture, edge, edge, edge, edge);
        skullTexture.setFilter(
            Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        preferences = Gdx.app.getPreferences("AoLALeaderboard");
        bestScore = preferences.getInteger("bestScore", 0);
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_W, WORLD_H, camera);
        soundManager = new SoundManager();
        setScreen(new MenuScreen(this));
    }
    public void resizeCamera(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }
    public int getBestScore() {
        return bestScore;
    }
    public void submitScore(int score) {
        if (score > bestScore) {
            bestScore = score;
            preferences.putInteger("bestScore", bestScore);
            preferences.flush();
        }
    }
    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode((int) WORLD_W, (int) WORLD_H);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
        super.render();
    }
    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        batch.dispose();
        shapeRenderer.dispose();
        menuFont.dispose();
        buttonNormalTexture.dispose();
        trophyTexture.dispose();
        uiPanel.dispose();
        titleFont.dispose();
        wingsTexture.dispose();
        buttonHoverTexture.dispose();
        skullTexture.dispose();
        soundManager.dispose();
    }
}
