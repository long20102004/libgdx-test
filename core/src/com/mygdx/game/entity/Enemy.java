package com.mygdx.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.GameScreen;
import com.mygdx.game.MyGdxGame;

public class Enemy extends GameEntity {
    public Enemy(float width, float height, Body body) {
        super(width, height, body);
        speed = 1f;
    }

    @Override
    public void update() {
    }

    public void update(Player player) {
//        moveTowardsPlayer(player);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {

    }

    public void moveTowardsPlayer(Player player) {
        if (Math.abs(this.body.getPosition().y - player.body.getPosition().y) >= 5) return;
        Vector2 direction = new Vector2(
                player.getBody().getPosition().x - body.getPosition().x,
                0
        );
        direction.nor(); // Normalize the direction vector
        body.setLinearVelocity(direction.scl(speed)); // Set the enemy's velocity
    }
}
