package AoLA.game.screens;

import AoLA.game.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen implements Screen {
    private final Main game;
    private final MenuButton startButton=new MenuButton("Start",new Color(0.16f,0.48f,0.24f,1f));
    private final MenuButton leaderboardButton=new MenuButton("Leaderboard",new Color(0.15f,0.30f,0.60f,1f));
    private final MenuButton creditsButton=new MenuButton("Credits",new Color(0.40f,0.25f,0.55f,1f));
    private final MenuButton quitButton=new MenuButton("Quit Game", new Color(0.62f, 0.20f, 0.17f, 1f));
    private final MenuButton[] buttons={startButton,leaderboardButton,creditsButton,quitButton};

    public MenuScreen(Main game) {
        this.game=game;
        MenuButton.applySkin(buttons,game.buttonNormal,game.buttonHover);
    }
    @Override
    public void show() {
        game.resizeCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
    }
    @Override
    public void render(float delta) {
        int width=Gdx.graphics.getWidth();
        int height=Gdx.graphics.getHeight();
        ScreenUtils.clear(0.07f, 0.04f, 0.05f, 1f);
        float scale=MenuButton.scaleFor(width,height);
        float top=MenuButton.layoutColumn(buttons,width,height);
        if (startButton.isClicked()){
            game.setScreen(new GameScreen(game));
            return;
        }
        if (quitButton.isClicked()) {
            Gdx.app.exit();
            return;
        }
        if (leaderboardButton.isClicked()){
            game.setScreen(new LeaderBoardScreen(game,this));
            return;
        }
        if (creditsButton.isClicked()){
            game.setScreen(new CreditsScreen(game,this));
            return;
        }
        ScreenUtils.clear(0.025f, 0.03f, 0.045f, 1f);
        float panelWidth = Math.min(380f, width * 0.85f);
        float panelHeight = Math.min(430f, height * 0.85f);
        float panelX = (width - panelWidth) / 2f;
        float panelY = (height - panelHeight) / 2f;
        game.batch.begin();
        game.batch.draw(game.uiPanel, panelX, panelY, panelWidth, panelHeight
        );
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(MenuButton button:buttons){
            button.drawShape(game.shapeRenderer);
        }
        game.shapeRenderer.end();
        game.batch.begin();
        MenuButton.drawCentered(game.batch,game.menuFont,"AoLA",2f*scale,width/2f,top+60f*scale);
        for (MenuButton button:buttons){
            button.drawText(game.batch,game.menuFont,scale);
        }
        game.batch.end();}
    @Override
    public void resize(int width, int height) {
        game.resizeCamera(width,height);
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
