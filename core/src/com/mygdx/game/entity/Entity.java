package com.mygdx.game.entity;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.GameScreen;
import com.mygdx.game.utilz.LightHandler;

import java.lang.reflect.Type;

import static com.mygdx.game.utilz.Constant.PPM;
import static com.mygdx.game.utilz.Constant.TILE_SIZE;

public class Entity extends Sprite {
    public static Entity INSTANCE;
    protected float x, y, velX, velY, speed;
    protected float width, height;
    protected Body body;
    protected int jumpCount;
    Animation<TextureRegion>[] animations;
    protected int[] animationLength;
    protected float stateTime = 0;
    protected int type;
    public static final int HERO = 0;
    public static final int ENEMY = 1;
    protected float attackRange = 3 * TILE_SIZE;
    protected boolean facingRight = true;
    protected int currentAction, idle, moving, attacking, dashing, jumping, falling, wallSlide, hit, dead;
    protected boolean isActive;
    protected int maxHealth = 100;
    protected int currentHealth = maxHealth;
    protected int maxPower = 100;
    protected int currentPower = maxPower;
    protected boolean isAttacked = false;
    protected Rectangle hitBox, attackBox;
    protected int damage;
    protected int mana = 10;
    ShapeRenderer renderer = new ShapeRenderer();
    public Entity(int type, String playerPicture, float width, float height,
                  float defaultWidth, float defaultHeight, Body body, int numberAction,
                  int idleAction, int numberIdleFrame,
                  int movingAction, int numberMovingFrame,
                  int attackingAction, int numberAttackingFrame,
                  int dashingAction, int numberDashingFrame,
                  int jumpingAction, int numberJumpingFrame,
                  int hit, int numberHitFrame,
                  int die, int numberDieFrame) {
        super(new Texture(Gdx.files.internal(playerPicture)));
        renderer.setAutoShapeType(true);
        this.type = type;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        hitBox = new Rectangle(body.getPosition().x, body.getPosition().y, width, height);
        attackBox = new Rectangle(body.getPosition().x, body.getPosition().y, width * 1.5f, height);
        this.body = body;
        if (type == ENEMY) this.speed = 1f;
        else this.speed = 4f;
        INSTANCE = this;
        isActive = true;

        animationLength = new int[numberAction];
        setIdle(idleAction, numberIdleFrame);
        setMovingAction(movingAction, numberMovingFrame);
        setAttackingAction(attackingAction, numberAttackingFrame);
        setDashing(dashingAction, numberDashingFrame);
        setJumping(jumpingAction, numberJumpingFrame);
        setHit(hit, numberHitFrame);
        setDead(die, numberDieFrame);
        currentAction = idle;

        Texture spriteSheet = new Texture(playerPicture);
        TextureRegion[][] tmpFrames = TextureRegion.split(spriteSheet, (int) defaultWidth, (int) defaultHeight);
        animations = new Animation[numberAction];
        for (int i = 0; i < numberAction; i++) {
            TextureRegion[] animationFrames = new TextureRegion[animationLength[i]];
            System.arraycopy(tmpFrames[i], 0, animationFrames, 0, animationLength[i]);
            animations[i] = new Animation<>(0.15f, animationFrames);
        }
    }
    public void drawHitbox(){
        renderer.begin();
        renderer.rect(hitBox.getX(), hitBox.getY(), hitBox.getWidth(), hitBox.getHeight());
        renderer.end();
    }
    public void update(GameScreen gameScreen) {
        if (!isActive) return;
        this.setOriginCenter();
        if (type == HERO) checkUserInput(gameScreen);
        else enemyAction(gameScreen);
        if (animations[currentAction].isAnimationFinished(stateTime)) {
            stateTime = 0;
            currentAction = idle;
            isAttacked = false;
        }
        stateTime += Gdx.graphics.getDeltaTime();
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        this.setPosition(x - this.getWidth() / 2, y - this.getHeight() / 2);
        hitBox.setPosition(x - this.getWidth() / 2, y - this.getHeight() / 2);
        attackBox.setPosition(x - this.getWidth() / 2, y - this.getHeight() / 2);

        if (type == HERO){
            if (facingRight) this.setX(x - this.width / 2.5f);
            else this.setX(x - this.width / 1.5f);
        }
        if (currentAction != 6) {
            TextureRegion currentFrame = animations[currentAction].getKeyFrame(stateTime);
            this.setRegion(currentFrame);
        }

        if (!facingRight && !this.isFlipX()) {
            // If it is, flip the sprite horizontally
            this.flip(true, false);
        } else if (facingRight && this.isFlipX()) {
            // If the velocity in the x direction is greater than 0 and the sprite is flipped, unflip it
            this.flip(true, false);
        }
    }

    public void enemyAction(GameScreen gameScreen) {
        Vector2 playerPos = gameScreen.getPlayer().getBody().getPosition();
        Vector2 enemyPos = body.getPosition();
        if (Math.abs(playerPos.y - enemyPos.y) >= TILE_SIZE * 2) return;
        float distanceToPlayer = body.getPosition().dst(playerPos);
        if (distanceToPlayer > attackRange) {
            currentAction = moving;
            float direction = (enemyPos.x < playerPos.x) ? 1 : -1;
            facingRight = direction == 1;
            body.setLinearVelocity(direction * speed, body.getLinearVelocity().y);
        } else currentAction = attacking;
    }

    private void checkUserInput(GameScreen gameScreen) {
        if (currentAction == moving) currentAction = idle;
        velX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentAction = moving;
            velX = 1;
            facingRight = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentAction = moving;
            velX = -1;
            facingRight = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && jumpCount < 2) {
            currentAction = jumping;
            float force = body.getMass() * 8;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            jumpCount++;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            currentAction = attacking;
            if (!isAttacked) playerAttack(gameScreen);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.K)){
            if(mana <= currentPower){
                updatePower(mana);
                currentAction = dashing;
            }
        }
        if (Math.abs(body.getLinearVelocity().y) < 0.01) {
            jumpCount = 0;
//            currentAction = idle;
        }
        body.setLinearVelocity(velX * speed, Math.min(body.getLinearVelocity().y, 25));
    }

    private void playerAttack(GameScreen gameScreen) {
        isAttacked = true;
        for (Entity entity : gameScreen.getEnemies()){
            if (attackBox.overlaps(entity.hitBox)){
                entity.updateHealth(-damage);
                entity.currentAction = hit;
                System.out.println("hit");
            }
        }
    }
    private void updateHealth(int damage){
        currentHealth += damage;
        System.out.println(currentHealth);
        if (currentHealth <= 0){
            currentHealth = 0;
            currentAction = dead;
            System.out.println("dead");
        }
    }

    private void updatePower(int mana){
        currentPower -= mana;

    }

    public void setMovingAction(int movingAction, int numberFrame) {
        this.moving = movingAction;
        animationLength[moving] = numberFrame;
    }

    public void setAttackingAction(int attackingAction, int numberFrame) {
        this.attacking = attackingAction;
        animationLength[attacking] = numberFrame;
    }

    public void setDashing(int dashing, int numberFrame) {
        this.dashing = dashing;
        animationLength[dashing] = numberFrame;
    }

    public void setJumping(int Jumping, int numberFrame) {
        this.jumping = Jumping;
        animationLength[jumping] = numberFrame;
    }

    public void setHit(int hit, int numberFrame) {
        this.hit = hit;
        animationLength[hit] = numberFrame;
    }

    public void setIdle(int idle, int numberFrame) {
        this.idle = idle;
        animationLength[idle] = numberFrame;
//        System.out.println(idle + " " + animationLength[idle]);
    }
    public void setDead(int dead, int numberFrame){
        this.dead = dead;
        animationLength[dead] = numberFrame;
    }

    public Body getBody() {
        return body;
    }
    public void setDamage(int damage){
        this.damage = damage;
    }
    public int getCurrentPower(){
        return currentPower;
    }
    public int getCurrentHealth(){
        return currentHealth;
    }
}