package AoLA.game.entities.Concrete;

import AoLA.game.Enums.Direction;
import AoLA.game.Enums.EnemyState;
import AoLA.game.entities.Abstract.Living;
import AoLA.game.managers.CollisionManager;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Living {
    private EnemyState currentState = EnemyState.PATROL;
    private Player target;
    private final CollisionManager collisionManager;

    //Patrol points
    private Vector2 pointA;
    private Vector2 pointB;
    private Vector2 currentPatrolTarget;

    //Specs
    private float detectionRadius;
    private float loseSightRadius;
    private float attackRange;
    private int attackDamage;
    private float attackCooldown;
    private float attackTimer;

    public Enemy(float startX, float startY, float endX, float endY,
                 float speed, int maxHealth, int attackDamage, String assetFolder, Player target,
                 CollisionManager collisionManager) {
        super(new Vector2(startX, startY), speed, maxHealth);

        this.target = target;
        this.collisionManager = collisionManager;
        this.attackDamage = attackDamage;
        this.pointA = new Vector2(startX, startY);
        this.pointB = new Vector2(endX, endY);
        this.currentPatrolTarget = pointB;

        initAnimations(
            assetFolder + "/down",
            assetFolder + "/up",
            assetFolder + "/left",
            assetFolder + "/right",
            4,
            0.15f
        );
    }

    @Override
    public void update(float delta) {
        if(!this.isAlive() || !target.isAlive()){
            return;
        }

        else if(this.attackTimer > 0){
            attackTimer -=delta;
        }

        float distance = position.dst(target.getPosition());
        if(distance <= attackRange){
            currentState = EnemyState.ATTACK;
        }

        else if (distance <= detectionRadius || (currentState == EnemyState.ATTACK && distance <= loseSightRadius)){
            currentState = EnemyState.CHASE;
        }

        else if(distance > loseSightRadius){
            currentState = EnemyState.PATROL;
        }

        switch (getCurrentState()){
            case PATROL:
                patrol(delta);
        }
    }

    private boolean canAct() {
        return isAlive() && target != null && target.isAlive();
    }

    private void updateCooldowns(float delta) {
        if(canAct()) {
            if (attackTimer > 0) {
                attackTimer -= delta;
            }
        }
    }

    private void updateState() {
        float distance = position.dst(target.getPosition());

        if (distance <= attackRange) {
            currentState = EnemyState.ATTACK;
        } else if (distance <= detectionRadius || (currentState == EnemyState.ATTACK && distance <= loseSightRadius)) {
            currentState = EnemyState.CHASE;
        } else if (distance > loseSightRadius) {
            currentState = EnemyState.PATROL;
        }
    }

    private void executeState(float delta) {
        switch (currentState) {
            case PATROL:
                patrol(delta);
                break;
            case CHASE:
                chase(delta);
                break;
            case ATTACK:
                attack();
                break;
        }
    }

    //Behaviors
    private void patrol (float delta){
        if(position.dst(currentPatrolTarget) < 5f){
            switchPatrolTarget();
            return;
        }
        moveTowards(currentPatrolTarget, speed * 0.7f, delta);
    }

    private void switchPatrolTarget(){
        if(currentPatrolTarget == pointB){
            currentPatrolTarget = pointA;
        }
        else{
            currentPatrolTarget = pointB;
        }
    }

    private void chase(float delta){
        moveTowards(target.getPosition(),speed,delta);
    }

    private void attack(){
        stopAnimation();
        Vector2 directionToTarget = new Vector2(target.getPosition()).sub(position);
        updateFacingDirection(directionToTarget);

        if(attackTimer <= 0f){
            target.takeDamage(attackDamage);
            attackTimer = attackCooldown;
        }
    }

    //Acts
    private void moveTowards(Vector2 destination, float moveSpeed, float delta) {
        Vector2 direction = new Vector2(destination).sub(position).nor();
        boolean moved = collisionManager.move(
            this,
            direction.x * moveSpeed * delta,
            direction.y * moveSpeed * delta
        );

        updateFacingDirection(direction);
        isMoving = moved;
        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }
    }

    private void updateFacingDirection(Vector2 direction) {
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            currentDirection = (direction.x > 0) ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentDirection = (direction.y > 0) ? Direction.UP : Direction.DOWN;
        }
    }

    private void stopAnimation() {
        isMoving = false;
        stateTime = 0f;
    }
    public EnemyState getCurrentState(){
        return this.currentState;
    }


}
