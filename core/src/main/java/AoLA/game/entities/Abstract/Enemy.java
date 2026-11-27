package AoLA.game.entities.Abstract;

import AoLA.game.Enums.Direction;
import AoLA.game.Enums.EnemyState;
import AoLA.game.entities.Concrete.Player;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy extends Living {
    private EnemyState currentState = EnemyState.PATROL;
    private Player target;

    //Patrol points
    private Vector2 pointA;
    private Vector2 pointB;
    private Vector2 currentPatrolTarget;

    //Specs
    protected float detectionRadius;
    protected float loseSightRadius;
    protected float attackRange;
    protected int attackDamage;
    protected float attackCooldown;
    protected float attackTimer;

    public Enemy(float startX, float startY, float endX, float endY,
                 float speed, int maxHealth, int attackDamage, String enemyName, Player target,float width,float height) {
        super(new Vector2(startX, startY), speed, maxHealth,width,height);

        this.target = target;
        this.attackDamage = attackDamage;
        this.pointA = new Vector2(startX, startY);
        this.pointB = new Vector2(endX, endY);
        this.currentPatrolTarget = pointB;

        String basePath = "enemy_asset/" + enemyName + "_asset/" + enemyName;

        initAnimations(
            basePath + "_down",
            basePath + "_up",
            basePath + "_left",
            basePath + "_right",
            4,
            0.15f
        );
    }

    @Override
    public void update(float delta) {
        updateCombatEffects(delta);
        if(!canAct()){
            return;
        }

        updateCooldowns(delta);
        updateState();
        executeState(delta);
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


    public EnemyState getCurrentState(){
        return this.currentState;
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

    protected void initCombatSpecs(float detectionRadius, float loseSightRadius, float attackRange, float attackCooldown){
        this.detectionRadius = detectionRadius;
        this.loseSightRadius = loseSightRadius;
        this.attackRange = attackRange;
        this.attackCooldown = attackCooldown;
    }

}
