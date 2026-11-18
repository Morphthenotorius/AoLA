package AoLA.game.entities.Concrete;

import AoLA.game.Enums.Direction;
import AoLA.game.entities.Abstract.Living;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Living {

    public Player(float x, float y) {
        super(new Vector2(x, y), 200f, 100);

        initAnimations(
            "akber.asset/akber.down",
            "akber.asset/akber.up",
            "akber.asset/akber.left",
            "akber.asset/akber.right",
            4,
            0.12f
        );
    }

    @Override
    public void update(float delta) {
        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            position.x -= speed * delta;
            currentDirection = Direction.LEFT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            position.x += speed * delta;
            currentDirection = Direction.RIGHT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            position.y += speed * delta;
            currentDirection = Direction.UP;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            position.y -= speed * delta;
            currentDirection = Direction.DOWN;
            isMoving = true;
        }

        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        updateBounds();
    }
}
