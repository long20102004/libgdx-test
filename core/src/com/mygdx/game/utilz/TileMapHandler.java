package com.mygdx.game.utilz;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.screen.GameScreen;
import com.mygdx.game.entity.Ghoul;
import com.mygdx.game.entity.SwordMan;
import com.mygdx.game.object.AnimatedObject;

import java.util.Random;

import static com.mygdx.game.utilz.Constant.ANIMATED_TREE.*;
import static com.mygdx.game.utilz.Constant.PPM;

public class TileMapHandler {
    private TiledMap tiledMap;
    private TiledMap menuBack;
    private GameScreen gameScreen;
    private Random random = new Random();

    public TileMapHandler(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public OrthogonalTiledMapRenderer setupMap() {
        tiledMap = new TmxMapLoader().load("map/map1.tmx");
        parseMapObject(tiledMap.getLayers().get("Object Layer 1").getObjects());
        return new OrthogonalTiledMapRenderer(tiledMap);
    }
    public OrthogonalTiledMapRenderer setUpMenu(){
        menuBack = new TmxMapLoader().load("map/menuback.tmx");
        parseMapObject(menuBack.getLayers().get("Object Layer 1").getObjects());
        return new OrthogonalTiledMapRenderer(menuBack);
    }
    public static void createMenuBack(){

    }
    private Body createBody(Rectangle rectangle, boolean isStatic){
        return BodyHandler.createBody(
                rectangle.getX() + rectangle.getWidth() / 2,
                rectangle.getY() + rectangle.getHeight() / 2,
                rectangle.getWidth(),
                rectangle.getHeight(),
                isStatic,
                gameScreen.getWorld()
        );
    }

    private void parseMapObject(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolygonMapObject) {
                createStaticBody((PolygonMapObject) mapObject);
            }
            if (mapObject instanceof PolylineMapObject) {
                createStaticBodyForPolyline((PolylineMapObject) mapObject);
            }
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectangleName = mapObject.getName();
                if (rectangleName == null) continue;

                if (rectangleName.equals("player")) {
                    Body body = createBody(rectangle, false);
                    gameScreen.setPlayer(
                            new SwordMan("Character/SwordHero.png", Constant.PLAYER.SWORD_HERO.WIDTH,
                                    Constant.PLAYER.SWORD_HERO.HEIGHT, Constant.PLAYER.SWORD_HERO.DEFAULT_WIDTH,
                                    Constant.PLAYER.SWORD_HERO.DEFAULT_HEIGHT, body, 27, Constant.PLAYER.SWORD_HERO.IDLE,
                                    Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.IDLE),
                                    Constant.PLAYER.SWORD_HERO.RUN_FAST, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.RUN_FAST),
                                    Constant.PLAYER.SWORD_HERO.SPIN_ATTACK, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.SPIN_ATTACK),
                                    Constant.PLAYER.SWORD_HERO.DASH, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.DASH),
                                    Constant.PLAYER.SWORD_HERO.JUMP, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.JUMP),
                                    Constant.PLAYER.SWORD_HERO.HIT, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.HIT),
                                    Constant.PLAYER.SWORD_HERO.DEATH, Constant.PLAYER.SWORD_HERO.getType(Constant.PLAYER.SWORD_HERO.DEATH))
                    );
                }
                if (rectangleName.contains("ghoul")) {
                    Body body = createBody(rectangle, false);
                    gameScreen.addEnemy(
                            new Ghoul("Enemies/Ghoul.png", Constant.GHOUL.WIDTH, Constant.GHOUL.HEIGHT,
                                    Constant.GHOUL.DEFAULT_WIDTH, Constant.GHOUL.DEFAULT_HEIGHT, body, 7,
                                    Constant.GHOUL.WAKE, Constant.GHOUL.getType(Constant.GHOUL.WAKE),
                                    Constant.GHOUL.MOVING, Constant.GHOUL.getType(Constant.GHOUL.MOVING),
                                    Constant.GHOUL.ATTACK, Constant.GHOUL.getType(Constant.GHOUL.ATTACK),
                                    6, 6, 6, 6,
                                    Constant.GHOUL.HIT, Constant.GHOUL.getType(Constant.GHOUL.HIT), Constant.GHOUL.DEAD, Constant.GHOUL.getType(Constant.GHOUL.DEAD))
                    );
                    if (rectangleName.contains("-light")){
                        gameScreen.addRayHandler(body, true);
                    }
                }
                if (rectangleName.contains("tree1")) {
                    Body body = createBody(rectangle, true);
                    gameScreen.addObject(new AnimatedObject("Objects/AnimatedTree1.png", 8, 4,
                            TREE1_WIDTH, TREE1_HEIGHT, TREE1_DEFAULT_WIDTH, TREE1_DEFAULT_HEIGHT, random.nextInt(1,5), body));
                    if (rectangleName.contains("-light")){
                        gameScreen.addRayHandler(body, true);
                    }
                }
                if (rectangleName.contains("bugs")){
                    Body body = createBody(rectangle, true);
                    gameScreen.addObject(new AnimatedObject("Objects/greenBug.png", 13, 1,
                            Constant.GREEN_BUG.DEFAULT_WIDTH, Constant.GREEN_BUG.DEFAULT_HEIGHT, Constant.GREEN_BUG.DEFAULT_WIDTH, Constant.GREEN_BUG.DEFAULT_HEIGHT, 1, body));
                    if (rectangleName.contains("-light")){
                        gameScreen.addRayHandler(body, true);
                    }
                }
                if (rectangleName.equals("light")){
                    Body body = createBody(rectangle, true);
                    gameScreen.addRayHandler(body, true);
                }

//                if (rectangleName.equals("flower")){
//                    Body body = createBody(rectangle, true);
//                    gameScreen.addObject(new AnimatedObject("Objects/Flower.png", ));
//                }
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

    private Shape createPolylineShape(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);
        return chainShape;
    }

    private void createStaticBodyForPolyline(PolylineMapObject polylineObject) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolylineShape(polylineObject);
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
    public TiledMap getTiledMap(){
        return tiledMap;
    }
}
