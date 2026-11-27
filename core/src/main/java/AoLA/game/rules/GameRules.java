package AoLA.game.rules;

import java.util.HashSet;
import java.util.Set;

public class GameRules {
    private final Set<Integer> collectedKeyIds = new HashSet<>();

    /** Records a key and returns true only the first time it is collected. */
    public boolean collectKey(int keyId) {
        return collectedKeyIds.add(keyId);
    }

    /** Checks whether the key for a door has been collected. */
    public boolean isDoorUnlocked(int keyId) {
        return collectedKeyIds.contains(keyId);
    }
    public int getCollectedKeyCount() {
        return collectedKeyIds.size();
    }
}
