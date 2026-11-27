package AoLA.game.entities.Concrete.Enemies;

import AoLA.game.entities.Abstract.Enemy;
import AoLA.game.entities.Concrete.Player;

public class Mummy extends Enemy {
    public Mummy(float startX, float startY, float endX, float endY,Player target) {
        super(startX, startY, endX, endY, 75f, 95, 12, "mummy", target,65f,75f);
        initCombatSpecs(300f, 400f, 60f, 1.7f);
    }
}
