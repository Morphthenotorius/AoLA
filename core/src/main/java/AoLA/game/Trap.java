package AoLA.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a floor spike trap that alternates between a safe closed state
 * and an active open state that damages the player on contact.
 */
public class Trap {
    private final Vector2 position;
    private final Rectangle bounds;
    private final int damage;

    private final float closedDuration;
    private final float openDuration;
    private float stateTimer;
    private boolean open;
    private float damageCooldown;

    /**
     * Creates a timed floor trap at the specified world coordinates.
     *
     * @param x              world x-coordinate in pixels
     * @param y              world y-coordinate in pixels
     * @param width          collision box width in pixels
     * @param height         collision box height in pixels
     * @param damage         hitpoints deducted when the player touches the open trap
     * @param closedDuration duration in seconds the trap stays retracted (safe)
     * @param openDuration   duration in seconds the trap stays extended (damaging)
     */
    public Trap(float x, float y, float width, float height, int damage,
                float closedDuration, float openDuration) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, width, height);
        this.damage = damage;
        this.closedDuration = closedDuration;
        this.openDuration = openDuration;
        this.stateTimer = 0f;
        this.open = false;
        this.damageCooldown = 0f;
    }

    /**
     * Advances the trap's open/closed cycle timer and hit cooldown each frame.
     *
     * @param delta elapsed frame time in seconds
     */
    public void update(float delta) {
        stateTimer += delta;
        if (damageCooldown > 0f) {
            damageCooldown -= delta;
        }

        if (!open && stateTimer >= closedDuration) {
            open = true;
            stateTimer = 0f;
        } else if (open && stateTimer >= openDuration) {
            open = false;
            stateTimer = 0f;
        }
    }

    /**
     * Checks whether the trap spikes are currently extended and ready to deal damage.
     *
     * @return true if the trap is open and its hit cooldown has expired
     */
    public boolean canDamage() {
        return open && damageCooldown <= 0f;
    }

    /**
     * Starts a cooldown period after dealing damage so the player is not hit every frame.
     *
     * @param cooldownSeconds cooldown duration in seconds
     */
    public void triggerCooldown(float cooldownSeconds) {
        this.damageCooldown = cooldownSeconds;
    }

    /**
     * Returns whether the trap spikes are currently open.
     *
     * @return true when open (damaging), false when closed (safe)
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Returns the world collision rectangle of the trap.
     *
     * @return trap bounding box
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Returns the amount of damage dealt when the trap is open.
     *
     * @return damage value in hitpoints
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Returns the world position of the trap.
     *
     * @return bottom-left position vector in pixels
     */
    public Vector2 getPosition() {
        return position;
    }
}
