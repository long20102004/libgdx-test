package com.mygdx.game.entity;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import static com.mygdx.game.utilz.Constant.PPM;

public class Player extends GameEntity {
    public static Player INSTANCE;
    private int jumpCount;
    private boolean isOnGround;
    public Player(float width, float height, Body body){
        super(width, height, body);
        this.speed = 4f;
        INSTANCE = this;
//        PolygonShape sensorShape = new PolygonShape();
//        sensorShape.setAsBox(width / 2, height / 10, new Vector2(0, -height / 2), 0);
//        FixtureDef sensorFixtureDef = new FixtureDef();
//        sensorFixtureDef.shape = sensorShape;
//        sensorFixtureDef.isSensor = true;
//        body.createFixture(sensorFixtureDef).setUserData(this);
//        sensorShape.dispose();
    }
    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        checkUserInput();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {

    }
    private void checkUserInput(){
        velX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) velX = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) velX = -1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && jumpCount < 2){
            float force = body.getMass() * 8;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            jumpCount++;
        }
        if (Math.abs(body.getLinearVelocity().y) < 0.01) jumpCount = 0;
        body.setLinearVelocity(velX * speed, Math.min(body.getLinearVelocity().y, 25));
    }
}
