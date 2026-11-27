package AoLA.game.screens;

import AoLA.game.HUD.HUD;
import AoLA.game.Main;
import AoLA.game.entities.Concrete.Player;
import AoLA.game.managers.CollisionManager;
import AoLA.game.rules.GameRules;
import AoLA.game.world.MazeMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private static final float PLAYER_HALF_SIZE = 32f;
    private static final float EXIT_REACH_DISTANCE = 48f;
    private final Main game;
    private Player player;
    private MazeMap maze;
    private GameRules gameRules;
    private CollisionManager collisionManager;
    private OrthographicCamera camera;
    private Viewport viewport;
    private HUD hud;
    private DoorArrow doorArrow;
    private final Texture pauseUiTexture;
    private final TextureRegion pausePanel;
    private boolean paused = false;
    private final MenuButton resumeButton = new MenuButton("Resume", new Color(0.16f, 0.48f, 0.24f, 1f));
    private final MenuButton restartButton = new MenuButton("Restart", new Color(0.62f, 0.20f, 0.17f, 1f));
    private final MenuButton leaderboardButton = new MenuButton("Leaderboard", new Color(0.15f, 0.30f, 0.60f, 1f));
    private final MenuButton exitButton = new MenuButton("Exit to Menu", new Color(0.30f, 0.30f, 0.34f, 1f));
    private final MenuButton[] pauseButtons = {
        resumeButton, restartButton, leaderboardButton, exitButton
    };
    public GameScreen(Main game) {
        this.game = game;
        MenuButton.applySkin(pauseButtons, game.buttonNormal, game.buttonHover);
        pauseUiTexture = new Texture(Gdx.files.internal("FreeHorrorUi.png"));
        pauseUiTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pausePanel = new TextureRegion(pauseUiTexture, 128, 132, 64, 72);
    }
    private void startNewGame() {
        if (player != null) {
            player.dispose();
        }
        if (hud != null) {
            hud.dispose();
        }
        if (doorArrow != null) {
            doorArrow.dispose();
        }
        if (maze != null) {
            maze.dispose();
        }
        maze = new MazeMap(game.batch);
        gameRules = new GameRules();
        collisionManager = new CollisionManager(maze, gameRules);
        doorArrow = new DoorArrow();
        Vector2 exit = maze.getExitPosition();
        doorArrow.setTarget(exit.x, exit.y);
        Vector2 spawn = maze.getPlayerSpawn();
        player = new Player(spawn.x, spawn.y, collisionManager);
        camera.position.set(spawn.x, spawn.y, 0);
        hud = new HUD(game.menuFont);
        paused = false;
    }
    @Override
    public void show() {
        if (maze == null) {
            camera = new OrthographicCamera();
            viewport = new FitViewport(1280, 720, camera);
            startNewGame();
        }
    }
    private boolean reachedExit() {
        if (gameRules.getCollectedKeyCount() <= 0) {
            return false;
        }
        Vector2 exit = maze.getExitPosition();
        float dx = player.getPosition().x + PLAYER_HALF_SIZE - exit.x;
        float dy = player.getPosition().y + PLAYER_HALF_SIZE - exit.y;
        return dx * dx + dy * dy <= EXIT_REACH_DISTANCE * EXIT_REACH_DISTANCE;
    }
    private void showWin() {
        int score = hud.getScore();
        game.submitScore(score, hud.getTimer().getElapsedSeconds());
        game.setScreen(new YouWin(game, score));
        dispose();
    }
    @Override
    public void render(float delta) {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
        }
        float pauseTop = 0f;
        if (!paused) {
            player.update(delta);
            int collectedCoins = collisionManager.collectCoins(player);
            for (int i = 0; i < collectedCoins; i++) {
                hud.addCoin();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) player.takeDamage(10);
            if (!player.isAlive()) {
                int score = hud.getScore();
                game.submitScore(score, hud.getTimer().getElapsedSeconds());
                game.setScreen(new GameOverScreen(game, score));
                dispose();
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
                showWin();
                return;
            }
            if (reachedExit()) {
                showWin();
                return;
            }
            hud.update(delta);
            doorArrow.update(delta);
            hud.getHealthBar().setHealth(player.getHealth());
            hud.getKeyIndicator().setKeyCount(gameRules.getCollectedKeyCount());
            game.submitScore(hud.getScore(), hud.getTimer().getElapsedSeconds());
        } else {
            pauseTop = MenuButton.layoutColumn(pauseButtons, width, height);
            if (handlePauseClicks()) {
                return;
            }
        }
        viewport.apply();
        camera.position.set(
            player.getPosition().x + PLAYER_HALF_SIZE,
            player.getPosition().y + PLAYER_HALF_SIZE,
            0f
        );
        camera.update();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);
        maze.render(camera);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        maze.renderObjects(game.batch);
        player.render(game.batch);
        doorArrow.render(game.batch, player.getPosition(), PLAYER_HALF_SIZE);
        game.batch.end();

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
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
        viewport.update(width, height, true);
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
        if (player != null) player.dispose();
        if (hud != null) hud.dispose();
        if (doorArrow != null) doorArrow.dispose();
        if (maze != null) maze.dispose();
        pauseUiTexture.dispose();
    }
}
