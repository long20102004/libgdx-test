package com.mygdx.game.utilz;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.game.GameScreen;
import com.mygdx.game.entity.Enemy;
import com.mygdx.game.entity.Player;
import com.mygdx.game.entity.SwordMan;

import static com.mygdx.game.utilz.Constant.PPM;

public class TileMapHandler {
    private TiledMap tiledMap;
    private GameScreen gameScreen;

    public TileMapHandler(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public OrthogonalTiledMapRenderer setupMap() {
        tiledMap = new TmxMapLoader().load("map/TileMap1.tmx");
        parseMapObject(tiledMap.getLayers().get("Object Layer 1").getObjects());
        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void parseMapObject(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolygonMapObject) {
                createStaticBody((PolygonMapObject) mapObject);
            }
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectangleName = mapObject.getName();
                if (rectangleName == null) continue;
                if (rectangleName.equals("player")) {
                    Body body = BodyHandler.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            gameScreen.getWorld()
                    );
                    gameScreen.setPlayer(
                            new SwordMan("Character/SwordHero.png", Constant.PLAYER.SWORD_HERO.WIDTH,
                                    Constant.PLAYER.SWORD_HERO.HEIGHT, Constant.PLAYER.SWORD_HERO.DEFAULT_WIDTH,
                                    Constant.PLAYER.SWORD_HERO.DEFAULT_HEIGHT, body, 27, Constant.PLAYER.SWORD_HERO.IDLE,
                                    Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.IDLE),
                                    Constant.PLAYER.SWORD_HERO.RUN, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.RUN),
                                    Constant.PLAYER.SWORD_HERO.SPIN_ATTACK, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.SPIN_ATTACK),
                                    Constant.PLAYER.SWORD_HERO.DASH, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.DASH),
                                    Constant.PLAYER.SWORD_HERO.JUMP, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.JUMP),
                                    Constant.PLAYER.SWORD_HERO.HIT, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.HIT))
                    );
                } else if (rectangleName.equals("enemy")) {
                    Body body = BodyHandler.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            gameScreen.getWorld()
                    );
                    gameScreen.setEnemy(new Enemy(rectangle.width, rectangle.height, body));
                }
            }
        }
    }

    private void createStaticBody(PolygonMapObject object) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(object);
        body.createFixture(shape, 1000);
        shape.dispose();
    }

    private Shape createPolygonShape(PolygonMapObject polygonMapObject) {
        float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];
        for (int i = 0; i < vertices.length / 2; i++) {
            Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
            worldVertices[i] = current;
        }
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(worldVertices);
        return polygonShape;
    }

}
