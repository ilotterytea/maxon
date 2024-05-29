package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.utils.AssetLoading;

public class SplashScreen implements Screen {
    final MaxonGame game;

    final Stage stage;
    final Skin skin;

    TextureAtlas brandAtlas;
    Image dev;
    ProgressBar bar;

    private boolean assetsLoaded = false;

    public SplashScreen(MaxonGame game) {
        this.game = game;

        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("MainSpritesheet.skin"));

        Table logoTable = new Table();
        logoTable.setFillParent(true);
        logoTable.align(Align.center);

        brandAtlas = new TextureAtlas(Gdx.files.internal("sprites/gui/ilotterytea.atlas"));

        dev = new Image(brandAtlas.findRegion("devOld"));
        dev.setSize(dev.getWidth() * 5f, dev.getHeight() * 5f);
        logoTable.add(dev).size(dev.getWidth(), dev.getHeight()).padBottom(60f).row();

        bar = new ProgressBar(0f, 100f, 1f, false, skin);
        logoTable.add(bar).width(dev.getWidth());

        stage.addActor(logoTable);

        AssetLoading.setup(game.assetManager);
        AssetLoading.queue(game.assetManager);
    }

    @Override public void show() {
        render(Gdx.graphics.getDeltaTime());
    }

    private void update() {
        if (game.assetManager.update() && !assetsLoaded) {
            stage.addAction(
                    Actions.sequence(
                            Actions.alpha(0.0f, 1f),
                            new Action() {
                                @Override
                                public boolean act(float v) {
                                    AssetLoading.registerItems(game.assetManager, game.locale);
                                    game.setScreen(new MenuScreen(game));
                                    dispose();
                                    return false;
                                }
                            }
                    )
            );
            assetsLoaded = true;
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
