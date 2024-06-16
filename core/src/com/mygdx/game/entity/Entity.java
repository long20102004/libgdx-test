package com.mygdx.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.screen.GameScreen;

import static com.mygdx.game.utilz.Constant.*;
import static java.lang.Math.abs;

public class Entity extends Sprite {
    private GameScreen gameScreen;
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
    protected int numberOfAction;

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

        falling = PLAYER.SWORD_HERO.FALL;

        this.type = type;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        hitBox = new Rectangle(body.getPosition().x, body.getPosition().y, width / 5f, height / 2f);
        attackBox = new Rectangle(body.getPosition().x, body.getPosition().y, width / 2f, height / 2f);
        this.body = body;
        if (type == ENEMY) this.speed = 1f;
        else this.speed = 4f;
        INSTANCE = this;
        isActive = true;

        animationLength = new int[numberAction + 1];
        if (type == HERO) animationLength[falling] = PLAYER.SWORD_HERO.getType(PLAYER.SWORD_HERO.FALL);

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

    public void update(GameScreen gameScreen) {
        if (!isActive) return;
        this.setOriginCenter();
        if (animations[currentAction].isAnimationFinished(stateTime) && isOnGround(gameScreen.getWorld())) {
            stateTime = 0;
            currentAction = idle;
            isAttacked = false;
        }
        if (type == HERO) {
            checkUserInput(gameScreen);
            if ((body.getLinearVelocity().y) <= -0.02) currentAction = falling;
        }
        else enemyAction(gameScreen);
//        System.out.println(currentAction);
        stateTime += Gdx.graphics.getDeltaTime();
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        this.setPosition((x - this.getWidth() / 2), (y - this.getHeight() / 2));
        hitBox.setPosition(x - hitBox.width / 2, y - hitBox.height / 2);
        attackBox.setPosition(x - attackBox.width / 2, y - attackBox.height / 2);

        if (facingRight) {
            this.setX(x - this.width / 2.5f);
            attackBox.setX(attackBox.getX() + attackBox.width / 2.5f);
        } else {
            this.setX(x - this.width / 1.5f);
            attackBox.setX(attackBox.getX() - attackBox.width / 2.5f);
        }

        if (currentAction != 6) {
            TextureRegion currentFrame = animations[currentAction].getKeyFrame(stateTime);
            this.setRegion(currentFrame);
        }

        if (!facingRight && !this.isFlipX()) {
            this.flip(true, false);
        } else if (facingRight && this.isFlipX()) {
            this.flip(true, false);
        }
    }

    public void enemyAction(GameScreen gameScreen) {
        Vector2 playerPos = gameScreen.getPlayer().getBody().getPosition();
        Vector2 enemyPos = body.getPosition();
        if (abs(playerPos.y - enemyPos.y) >= TILE_SIZE * 2) return;
        float distanceToPlayer = body.getPosition().dst(playerPos);
        if (distanceToPlayer > attackRange) {
            currentAction = moving;
            float direction = (enemyPos.x < playerPos.x) ? 1 : -1;
            facingRight = direction == 1;
            body.setLinearVelocity(direction * speed, body.getLinearVelocity().y);
        } else if (currentAction == idle) {
            currentAction = attacking;
            if (!isAttacked) hurtPlayer(gameScreen);
        }
    }

    private void hurtPlayer(GameScreen gameScreen) {
        isAttacked = true;
        if (attackBox.overlaps(gameScreen.getPlayer().getHitBox())){
            gameScreen.getPlayer().updateHealth(-damage);
            gameScreen.getPlayer().currentAction = gameScreen.getPlayer().hit;
        }
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
        if (Gdx.input.isKeyPressed(Input.Keys.S) && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (isTileAtY(body.getPosition().y - 5 * TILE_SIZE, gameScreen)) return;
            makePlayerSensor();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    makePlayerSolid();
                }
            }, 0.5f);
        }
        if (abs(body.getLinearVelocity().y) < 0.01) {
            jumpCount = 0;
//            currentAction = idle;
        }
        body.setLinearVelocity(velX * speed, Math.min(body.getLinearVelocity().y, 25));
    }

    private boolean isTileAtY(float y, GameScreen gameScreen) {
        // Convert the player's position from world coordinates to tile coordinates
        int tileX = (int) (body.getPosition().x / TILE_SIZE);
        int tileY = (int) (y / TILE_SIZE);

        // Get the tile layer
        TiledMapTileLayer layer = (TiledMapTileLayer) gameScreen.getTileMapHandler().getTiledMap().getLayers().get("tiles");

        // Get the tile at the player's position
        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);

        // Check if there is a tile at the player's position
        return cell != null && cell.getTile() != null;
    }

    private void makePlayerSensor() {
        // Create a sensor fixture if it doesn't exist yet
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }
    }

    private void makePlayerSolid() {
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(false);
        }
    }
    
    private void playerAttack(GameScreen gameScreen) {
        isAttacked = true;
        for (Entity entity : gameScreen.getEnemies()) {
            if (attackBox.overlaps(entity.hitBox)) {
                entity.updateHealth(-damage);
                entity.currentAction = entity.hit;
            }
        }
    }

    private void updateHealth(int damage) {
        currentHealth += damage;
        System.out.println(currentHealth);
        if (currentHealth <= 0) {
            currentHealth = 0;
            currentAction = dead;
        }
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

    public void setDead(int dead, int numberFrame) {
        this.dead = dead;
        animationLength[dead] = numberFrame;
    }

    public Body getBody() {
        return body;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public Rectangle getAttackBox() {
        return attackBox;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void setFalling(int falling, int numberFrames) {
        this.falling = falling;
        animationLength[falling] = numberFrames;
        numberOfAction++;
    }

    public void setDead(int dead) {
        this.dead = dead;
    }
    private boolean isOnGround(World world) {
        final boolean[] isOnGround = new boolean[1];

        // Define the start and end points of the ray. Start at the player's feet and end a little bit below.
        Vector2 rayStart = body.getPosition();
        Vector2 rayEnd = new Vector2(body.getPosition().x, body.getPosition().y - 0.1f);

        // Define the callback function
        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                isOnGround[0] = true;
                return fraction;
            }
        };

        // Perform the ray cast
        world.rayCast(callback, rayStart, rayEnd);

        return isOnGround[0];
    }
}
