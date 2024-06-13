package com.mygdx.game.entity;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.HashMap;
import java.util.Map;

import static com.mygdx.game.utilz.Constant.PPM;

public class Player extends Sprite {
    public static Player INSTANCE;
    protected float x, y, velX, velY, speed;
    protected float width, height;
    protected Body body;
    protected int jumpCount;
    Animation<TextureRegion>[] animations;
    protected int[] animationLength;
    protected float stateTime = 0;
    protected int currentAction, idle, moving, attacking, dashing, jumping, falling, wallSlide, hit, dead;

    public Player(String playerPicture, float width, float height,
                  float defaultWidth, float defaultHeight, Body body, int numberAction,
                  int idleAction, int numberIdleFrame,
                  int movingAction, int numberMovingFrame,
                  int attackingAction, int numberAttackingFrame,
                  int dashingAction, int numberDashingFrame,
                  int jumpingAction, int numberJumpingFrame,
                  int hit, int numberHitFrame) {
        super(new Texture(Gdx.files.internal(playerPicture)));
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.body = body;
        this.speed = 4f;
        INSTANCE = this;

        animationLength = new int[numberAction];
        setIdle(idleAction, numberIdleFrame);
        setMovingAction(movingAction, numberMovingFrame);
        setAttackingAction(attackingAction, numberAttackingFrame);
        setDashing(dashingAction, numberDashingFrame);
        setJumping(jumpingAction, numberJumpingFrame);
        setHit(hit, numberHitFrame);
//        System.out.println(idleAction.);
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

    public void update() {
        if (animations[currentAction].isAnimationFinished(stateTime)) {
            stateTime = 0;
            currentAction = idle;
        }
        stateTime += Gdx.graphics.getDeltaTime();
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        this.setPosition(x - this.getWidth() / 3, y - this.getHeight() / 3);
        checkUserInput();
        TextureRegion currentFrame = animations[currentAction].getKeyFrame(stateTime);
        this.setRegion(currentFrame);
    }


    private void checkUserInput() {
        velX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentAction = moving;
            velX = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentAction = moving;
            velX = -1;
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
        }
        if (Math.abs(body.getLinearVelocity().y) < 0.01) jumpCount = 0;
        body.setLinearVelocity(velX * speed, Math.min(body.getLinearVelocity().y, 25));
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
        this.jumping = jumpCount;
        animationLength[jumping] = numberFrame;
    }

    public void setHit(int hit, int numberFrame) {
        this.hit = hit;
        animationLength[hit] = numberFrame;
    }

    public void setIdle(int idle, int numberFrame) {
        this.idle = idle;
        animationLength[idle] = numberFrame;
    }

    public Body getBody() {
        return body;
    }
}
