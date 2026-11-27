package AoLA.game.entities.Abstract;

import AoLA.game.entities.Concrete.Player;

public interface EnemyFactory<T extends Enemy> {
    T create(float startX, float startY, float endX, float endY, Player target);
}
