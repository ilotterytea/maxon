package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.utils.AssetLoading;

public class SplashScreen implements Screen {
    final MaxonGame game;

    final Stage stage;
    final Skin skin;

    TextureAtlas brandAtlas;
    Image dev, pub;
    ProgressBar bar;

    public SplashScreen(MaxonGame game) {
        this.game = game;

        this.stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.skin = new Skin(Gdx.files.internal("MainSpritesheet.skin"));

        Table logoTable = new Table();

        logoTable.setSize(stage.getWidth(), stage.getHeight());
        logoTable.setPosition(0, 0);
        logoTable.align(Align.center);

        brandAtlas = new TextureAtlas(Gdx.files.internal("sprites/gui/ilotterytea.atlas"));

        pub = new Image(brandAtlas.findRegion("org"));
        logoTable.add(pub).size(pub.getWidth() * 5f, pub.getHeight() * 5f).pad(16f).row();

        dev = new Image(brandAtlas.findRegion("devOld"));
        logoTable.add(dev).size(dev.getWidth() * 5f, dev.getHeight() * 5f).row();

        bar = new ProgressBar(0f, 100f, 1f, false, skin);
        logoTable.add(bar).size(dev.getWidth() * 5f, 24f);

        stage.addActor(logoTable);

        AssetLoading.queue(game.assetManager);
    }

    @Override public void show() {
        render(Gdx.graphics.getDeltaTime());
    }

    private void update() {
        if (game.assetManager.update()) {
            AssetLoading.registerItems(game.assetManager, game.locale);
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act(delta);

        update();
        bar.setValue(100f / (game.assetManager.getQueuedAssets() + 1));
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
    }
}
