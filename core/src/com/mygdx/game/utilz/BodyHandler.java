package com.mygdx.game.utilz;

import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.game.utilz.Constant.PPM;

public class BodyHandler {
    public static Body createBody(float x, float y, float width, float height, boolean isStatic, World world){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.fixedRotation = true;
        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;
        if (isStatic) fixtureDef.filter.categoryBits = 0;
//        fixtureDef.density = 10000f;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }
}
