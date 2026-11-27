package AoLA.game.entities.Concrete.Enemies;

import AoLA.game.entities.Abstract.Enemy;
import AoLA.game.entities.Concrete.Player;

public class Skeleton extends Enemy {
    public Skeleton(float startX, float startY, float endX, float endY, Player target) {
        super(startX, startY, endX, endY, 90f, 70, 10, "skeleton", target, 85f,80f);
        initCombatSpecs(250f,350f, 60f, 1.2f);
    }
}
