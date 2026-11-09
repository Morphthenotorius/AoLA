package AoLA.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class Main extends ApplicationAdapter {
    static final int W = 31, H = 21; // must be odd
    private ShapeRenderer shapes;
    private final Matrix4 proj = new Matrix4();
    private final Random rnd = new Random();
    private int[][] maze;

    @Override
    public void create() {
        shapes = new ShapeRenderer();
        maze = MazeGenerator.generate(W, H, rnd);
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            maze = MazeGenerator.generate(W, H, rnd);
        }

        float sw = Gdx.graphics.getWidth(), sh = Gdx.graphics.getHeight();
        proj.setToOrtho2D(0, 0, sw, sh);
        shapes.setProjectionMatrix(proj);

        float cell = Math.min(sw / W, sh / H);
        float ox = (sw - cell * W) / 2f, oy = (sh - cell * H) / 2f;

        ScreenUtils.clear(0.08f, 0.08f, 0.1f, 1f);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                shapes.setColor(maze[y][x] == 1 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                shapes.rect(ox + x * cell, oy + (H - 1 - y) * cell, cell, cell);
            }
        }
        shapes.setColor(Color.GREEN);
        shapes.rect(ox + cell, oy + (H - 2) * cell, cell, cell);
        shapes.setColor(Color.RED);
        shapes.rect(ox + (W - 2) * cell, oy + cell, cell, cell);
        shapes.end();
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }
}
