package AoLA.game.screens;

import AoLA.game.HUD.HUD;
import AoLA.game.Main;
import AoLA.game.entities.Concrete.Player;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import AoLA.game.screens.DoorArrow;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class GameScreen implements Screen {
    private final Main game;
    private Player player;
    private HUD hud;
    private DoorArrow doorArrow;
    private Texture pauseUiTexture;
    private TextureRegion pausePanel;
    private boolean paused=false;
    private final MenuButton resumeButton= new MenuButton("Resume", new Color(0.16f,0.48f,0.24f,1f));
    private final MenuButton restartButton= new MenuButton("Restart", new Color(0.62f,0.20f,0.17f,1f));
    private final MenuButton leaderboardButton=new MenuButton("LeaderBoard",new Color(0.15f,0.30f,0.60f,1));
    private final MenuButton exitButton=new MenuButton("Exit to Menu",new Color(0.30f,0.30f,0.34f,1f));
    private final MenuButton[] pauseButtons={
        resumeButton, restartButton, leaderboardButton, exitButton
    };
    public GameScreen(Main game) {
        this.game = game;
        MenuButton.applySkin(pauseButtons, game.buttonNormal,game.buttonHover);
        pauseUiTexture = new Texture(Gdx.files.internal("FreeHorrorUi.png"));
        pauseUiTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pausePanel = new TextureRegion(pauseUiTexture, 128, 132, 64, 72);
        startNewGame();
    }
    private void startNewGame(){
        if (player != null){
            player.dispose();
        }
        if(hud != null){
            hud.dispose();
        }
        if (doorArrow != null) {
            doorArrow.dispose();
        }
        doorArrow = new DoorArrow();
        doorArrow.setTarget(500, 350);   // TEST: müvəqqəti qapı mövqeyi
        player=new Player(100,100);
        hud=new HUD(game.menuFont);
        paused=false;
    }
    @Override
    public void show() {
        game.resizeCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
    }
    public void render(float delta) {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
        }
        float pauseTop = 0f;
        if (!paused) {
            player.update(delta);
            player.update(delta);
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) player.takeDamage(10);
            if (!player.isAlive()) {
                int score = hud.getScore();
                game.submitScore(score);
                game.setScreen(new GameOverScreen(game, score));
                dispose();
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
                game.setScreen(new YouWin(game, hud.getScore()));
                return;
            }
            hud.update(delta);
            doorArrow.update(delta);
            hud.getHealthBar().setHealth(player.getHealth());
            game.submitScore(hud.getScore());
        } else {
            pauseTop = MenuButton.layoutColumn(pauseButtons, width, height);
            if (handlePauseClicks()) {
                return;
            }
        }
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);
        game.batch.begin();
        player.render(game.batch);
        doorArrow.render(game.batch, player.getPosition(), 32f);
        hud.render(game.batch, 0, 0, width, height);
        game.batch.end();
        if (paused) {
            drawPauseMenu(width, height, pauseTop);
        }
    }
    private boolean handlePauseClicks() {
        if (resumeButton.isClicked()) {
            paused = false;
        } else if (restartButton.isClicked()) {
            startNewGame();
        } else if (leaderboardButton.isClicked()) {
            game.setScreen(new LeaderBoardScreen(game, this));
            return true;
        } else if (exitButton.isClicked()) {
            game.setScreen(new MenuScreen(game));
            dispose();
            return true;
        }
        return false;
    }
    private void drawPauseMenu(int width, int height, float top) {
        float scale = MenuButton.scaleFor(width, height);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, 0.7f);
        game.shapeRenderer.rect(0f, 0f, width, height);
        game.shapeRenderer.end();
        float panelWidth = Math.min(380f, width * 0.85f);
        float panelHeight = Math.min(430f, height * 0.85f);
        float panelX = (width - panelWidth) / 2f;
        float panelY = (height - panelHeight) / 2f;
        game.batch.begin();
        game.batch.draw(pausePanel, panelX, panelY, panelWidth, panelHeight);
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (MenuButton button : pauseButtons) {
            button.drawShape(game.shapeRenderer);
        }
        game.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        game.batch.begin();
        MenuButton.drawCentered(game.batch, game.menuFont, "PAUSED", 2f * scale, width / 2f, top + 60f * scale);
        for (MenuButton button : pauseButtons) {
            button.drawText(game.batch, game.menuFont, scale);
        }
        game.batch.end();
    }
    @Override
    public void resize(int width, int height) {
        game.resizeCamera(width, height);
    }
    @Override
    public void pause() {
    }
    @Override
    public void resume() {
    }
    @Override
    public void hide() {
    }
    @Override
    public void dispose() {
        player.dispose();
        hud.dispose();
        doorArrow.dispose();
        pauseUiTexture.dispose();
    }
}
