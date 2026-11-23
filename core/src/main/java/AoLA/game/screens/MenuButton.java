package AoLA.game.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MenuButton {
    private final String text;
    private final Color color;
    private final Rectangle bounds=new Rectangle();
    public MenuButton(String text, Color color) {
        this.text = text;
        this.color = color;
    }
    public void setBounds(float centerX, float centerY, float width, float height) {
        bounds.set(centerX-width/2, centerY-height/2, width, height);
    }
    public boolean isClicked(){
        if(!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            return false;
        }
        float mouseX=Gdx.input.getX();
        float mouseY=Gdx.graphics.getHeight() - Gdx.input.getY();
        return bounds.contains(mouseX, mouseY);
    }
    public void drawShape(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(color);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    public void drawText(SpriteBatch batch, BitmapFont font, float scale) {
        font.getData().setScale(scale);
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, text);
        float textX = bounds.x + (bounds.width - layout.width) / 2f;
        float textY = bounds.y + (bounds.height + layout.height) / 2f;
        font.draw(batch, text, textX, textY);
    }
    public static float scaleFor(float screenWidth, float screenHeight){
        return Math.max(0.45f,Math.min(1.0f,Math.min(screenWidth/900f,screenHeight/600f)));
    }
    public static float layoutColumn(MenuButton[] button, float screenWidth, float screenHeight){
        float scale=scaleFor(screenWidth,screenHeight);
        float buttonWidth=Math.min(420f,screenWidth*0.92f);
        float buttonHeight=Math.min(55f,screenHeight*0.14f);
        float gap=25f*scale;
        float totalHeight=button.length*buttonHeight+(button.length-1)*gap;
        float top=screenHeight/2f+totalHeight/2f-20f*scale;
        for(int i=0; i<button.length; i++){
            float centerY=top-buttonHeight/2f-i*(buttonHeight+gap);button[i].setBounds(screenWidth/2f,centerY,buttonWidth,buttonHeight);
        }
        return top;
    }
    public static void layoutBottom(MenuButton button, float screenWidth, float screenHeight){
        float buttonWidth=Math.min(220f,screenWidth*0.75f);
        float buttonHeight=Math.min(55f,screenHeight*0.14f);
        button.setBounds(screenWidth/2f,buttonHeight*1.2f,buttonWidth,buttonHeight);
    }
    public static void drawCentered(SpriteBatch batch, BitmapFont font, String text, float scale, float centerX, float topY) {
        font.getData().setScale(scale);
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(batch, text, centerX - layout.width / 2f, topY);
    }

}
