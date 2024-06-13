package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.entity.Enemy;
import com.mygdx.game.entity.Player;
import com.mygdx.game.utilz.TileMapHandler;

import static com.mygdx.game.utilz.Constant.*;

public class GameScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private OrthographicCamera backgroundCamera;
    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TileMapHandler tileMapHandler;
    private Texture background;
    private Player player;
    private Enemy enemy;
    public GameScreen(OrthographicCamera camera) {
        System.out.println("Screen is created");
        this.camera = camera;
        backgroundCamera = new OrthographicCamera();
        backgroundCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        backgroundCamera.update();
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-25f), false);
        this.tileMapHandler = new TileMapHandler(this);
        this.orthogonalTiledMapRenderer = tileMapHandler.setupMap();
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        background = new Texture("background3.png");
    }
    private void update(){
        world.step(1/60f, 6, 2);
        cameraUpdate();
        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
        player.update();
        enemy.update(player);
    }

    private void cameraUpdate() {
        Vector3 position = camera.position;
        position.x = Math.round(player.getBody().getPosition().x * PPM * 10) / 10f;
        position.y = Math.round(player.getBody().getPosition().y * PPM * 10) / 10f;
        camera.position.set(position);
        camera.update();
    }

    @Override
    public void render(float delta){
        this.update();
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(backgroundCamera.combined);
        batch.begin();
        batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.end();
        orthogonalTiledMapRenderer.render();
        box2DDebugRenderer.render(world,camera.combined.scl(PPM));
    }

    public World getWorld() {
        return world;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public Player getPlayer() {
        return player;
    }
}
