package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends Game {
    public static MyGdxGame INSTANCE;
    public int SCREEN_WIDTH, SCREEN_HEIGHT;
    OrthographicCamera camera;
    public MyGdxGame(){
        INSTANCE = this;
    }

    @Override
    public void create() {
//        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        System.out.println("Game is created");
        camera = new OrthographicCamera();
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        setScreen(new GameScreen(camera));
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose() {

    }
}