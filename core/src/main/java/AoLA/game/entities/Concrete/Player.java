package AoLA.game.entities.Concrete;

import AoLA.game.Enums.Direction;
import AoLA.game.entities.Abstract.Living;
import AoLA.game.managers.CollisionManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Living {
    private final CollisionManager collisionManager;

    public Player(float x, float y, CollisionManager collisionManager) {
        super(new Vector2(x, y), 250f, 100);
        this.collisionManager = collisionManager;

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
            isMoving |= collisionManager.move(this, -speed * delta, 0f);
            currentDirection = Direction.LEFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            isMoving |= collisionManager.move(this, speed * delta, 0f);
            currentDirection = Direction.RIGHT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            isMoving |= collisionManager.move(this, 0f, speed * delta);
            currentDirection = Direction.UP;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            isMoving |= collisionManager.move(this, 0f, -speed * delta);
            currentDirection = Direction.DOWN;
        }

        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        updateBounds();
        collisionManager.collectKeys(this);
    }
}
