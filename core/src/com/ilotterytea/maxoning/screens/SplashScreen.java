package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.utils.AssetLoading;
import com.ilotterytea.maxoning.utils.OsUtils;
import com.ilotterytea.maxoning.utils.math.Math;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;

import java.io.IOException;
import java.util.ArrayList;

public class SplashScreen implements Screen {

    final MaxonGame game;

    final Stage stage;
    final Skin skin;

    TextureAtlas brandAtlas, envAtlas;
    ArrayList<Sprite> contribList;
    Image dev, pub;

    public SplashScreen(MaxonGame game) {
        this.game = game;

        this.stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.skin = new Skin(Gdx.files.internal("main.skin"));

        Table logoTable = new Table();

        logoTable.setSize(stage.getWidth(), stage.getHeight());
        logoTable.setPosition(0, 0);
        logoTable.align(Align.center);

        brandAtlas = new TextureAtlas(Gdx.files.internal("sprites/gui/ilotterytea.atlas"));
        envAtlas = new TextureAtlas(Gdx.files.internal("sprites/env/environment.atlas"));
        contribList = new ArrayList<>();

        pub = new Image(brandAtlas.findRegion("org"));
        logoTable.add(pub).size(pub.getWidth() * 5f, pub.getHeight() * 5f).pad(16f).row();

        dev = new Image(brandAtlas.findRegion("devOld"));
        logoTable.add(dev).size(dev.getWidth() * 5f, dev.getHeight() * 5f);

        stage.addActor(logoTable);

        AssetLoading.queue(game.assetManager);
    }

    @Override public void show() {
        int size = 64;
        for (int i = 0; i < stage.getHeight() / size; i++) {
            for (int j = 0; j < stage.getWidth() / size; j++) {
                Sprite spr = new Sprite(envAtlas.findRegion("tile"));
                spr.setSize(size, size);
                spr.setPosition(size * j, size * i);
                switch (Math.getRandomNumber(0, 5)) {
                    case 0: spr.setColor(Color.SKY); break;
                    case 1: spr.setColor(Color.PURPLE); break;
                    case 2: spr.setColor(Color.PINK); break;
                    case 3: spr.setColor(Color.CHARTREUSE); break;
                    case 4: spr.setColor(Color.ORANGE); break;
                }
                spr.setAlpha(0.25f);
                contribList.add(spr);
            }
        }

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                for (Sprite spr : contribList) {
                    switch (Math.getRandomNumber(0, 5)) {
                        case 0: spr.setColor(Color.SKY); break;
                        case 1: spr.setColor(Color.PURPLE); break;
                        case 2: spr.setColor(Color.PINK); break;
                        case 3: spr.setColor(Color.CHARTREUSE); break;
                        case 4: spr.setColor(Color.ORANGE); break;
                    }
                    spr.setAlpha(0.25f);
                }
            }
        }, 1f, 1f);

        render(Gdx.graphics.getDeltaTime());
    }

    private void update() {
        if (game.assetManager.update()) {
            AssetLoading.registerItems(game.assetManager, game.locale);
            if (OsUtils.isAndroid || OsUtils.isIos) {
                try {
                    game.setScreen(new GameScreen(game, GameDataSystem.load("latest.sav"), -1));
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                game.setScreen(new MenuScreen(game));
            }
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        for (Sprite spr : contribList) {
            spr.draw(game.batch);
        }
        game.batch.end();

        stage.draw();
        stage.act(delta);

        update();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() {
        brandAtlas.dispose();
        envAtlas.dispose();
    }
}
