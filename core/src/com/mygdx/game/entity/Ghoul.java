package com.mygdx.game.entity;

import com.badlogic.gdx.physics.box2d.Body;

public class Ghoul extends Entity{
    public Ghoul(String playerPicture, float width, float height, float defaultWidth, float defaultHeight, Body body, int numberAction, int idleAction, int numberIdleFrame, int movingAction, int numberMovingFrame, int attackingAction, int numberAttackingFrame, int dashingAction, int numberDashingFrame, int jumpingAction, int numberJumpingFrame, int hit, int numberHitFrame) {
        super(Entity.ENEMY, playerPicture, width, height, defaultWidth, defaultHeight, body, numberAction, idleAction, numberIdleFrame, movingAction, numberMovingFrame, attackingAction, numberAttackingFrame, dashingAction, numberDashingFrame, jumpingAction, numberJumpingFrame, hit, numberHitFrame);
    }
}
