package AoLA.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents an interactive door inside the maze parsed from a Tiled map object.
 */
public class RoomDoor {
    private final String name;
    private final Vector2 position;
    private final Rectangle bounds;
    private boolean locked;
    private boolean open;
    private final int doorId;
    private final int keyId;

    /**
     * Creates a room door instance from Tiled object data.
     *
     * @param name   object name from Tiled (e.g., "room2_door")
     * @param x      world x-coordinate in pixels
     * @param y      world y-coordinate in pixels
     * @param width  door width in pixels
     * @param height door height in pixels
     * @param locked whether the door starts locked
     * @param doorId identifier of this door
     * @param keyId  matching key identifier required if locked
     */
    public RoomDoor(String name, float x, float y, float width, float height,
                    boolean locked, int doorId, int keyId) {
        this.name = name;
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x, y, width, height);
        this.locked = locked;
        this.open = !locked;
        this.doorId = doorId;
        this.keyId = keyId;
    }

    /**
     * Unlocks and opens the door.
     */
    public void unlock() {
        this.locked = false;
        this.open = true;
    }

    /**
     * Returns the door's name from Tiled.
     *
     * @return door name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the door's world collision rectangle.
     *
     * @return bounding rectangle
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Returns the door's bottom-left world position.
     *
     * @return position vector
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Returns whether the door is currently locked.
     *
     * @return true if locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Returns whether the door is currently open and passable.
     *
     * @return true if open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Returns this door's ID.
     *
     * @return door ID
     */
    public int getDoorId() {
        return doorId;
    }

    /**
     * Returns the key ID required to unlock this door.
     *
     * @return matching key ID
     */
    public int getKeyId() {
        return keyId;
    }
}
