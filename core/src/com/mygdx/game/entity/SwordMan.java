package com.mygdx.game.entity;

import com.badlogic.gdx.physics.box2d.Body;

public class SwordMan extends Entity {
    public SwordMan(String playerPicture, float width, float height, float defaultWidth, float defaultHeight, Body body, int numberAction, int idleAction, int numberIdleFrame, int movingAction, int numberMovingFrame, int attackingAction, int numberAttackingFrame, int dashingAction, int numberDashingFrame, int jumpingAction, int numberJumpingFrame, int hit, int numberHitFrame) {
        super(Entity.HERO, playerPicture, width, height, defaultWidth, defaultHeight, body, numberAction, idleAction, numberIdleFrame, movingAction, numberMovingFrame, attackingAction, numberAttackingFrame, dashingAction, numberDashingFrame, jumpingAction, numberJumpingFrame, hit, numberHitFrame);
    }
}
