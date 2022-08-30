package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.ui.AnimatedImage;
import com.ilotterytea.maxoning.anim.SpriteUtils;
import com.ilotterytea.maxoning.utils.AssetLoading;

public class AssetLoadingScreen implements Screen {
    final MaxonGame game;

    final Stage stage;
    final Skin skin;
    final AnimatedImage animatedMaxon;
    final Label loadingLabel;

    final Texture M4x0nnes;

    public AssetLoadingScreen(MaxonGame game) {
        this.game = game;

        this.M4x0nnes = new Texture("sprites/sheet/loadingCircle.png");

        this.skin = new Skin(Gdx.files.internal("main.skin"));
        this.stage = new Stage(new ScreenViewport());

        this.loadingLabel = new Label("Loading...", skin);

        TextureRegion[] txrr = SpriteUtils.splitToTextureRegions(M4x0nnes, 112, 112, 10, 5);
        this.animatedMaxon = new AnimatedImage(txrr);

        animatedMaxon.setPosition(Gdx.graphics.getWidth() - animatedMaxon.getWidth() * 0.25f - 8, 8);
        animatedMaxon.setScale(0.25f);

        loadingLabel.setPosition(animatedMaxon.getWidth() * 0.25f + loadingLabel.getX() + 16, 8);

        stage.addActor(animatedMaxon);
        //stage.addActor(loadingLabel);

        AssetLoading.queue(game.assetManager);
    }

    @Override public void show() { render(Gdx.graphics.getDeltaTime()); }

    private void update() {
        if (game.assetManager.update()) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    game.setScreen(new SplashScreen(game));
                    dispose();
                }
            }, 1f);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        loadingLabel.setText(String.format("%s%% (Loaded %s assets)", MathUtils.floor(game.assetManager.getProgress() * 100), game.assetManager.getLoadedAssets()));

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
    @Override public void dispose() {}
}
