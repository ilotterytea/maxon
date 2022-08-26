package com.ilotterytea.maxoning.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ilotterytea.maxoning.MaxonConstants;
import com.ilotterytea.maxoning.MaxonGame;

public class SplashScreen implements InputProcessor, Screen {

    final MaxonGame game;

    final Stage stage;
    final Skin skin;

    final Image whiteSquare, dev;
    final Label infoLabel;

    final Music introMusic;

    public SplashScreen(MaxonGame game) {
        this.game = game;

        this.introMusic = game.assetManager.get("mus/menu/mus_menu_intro.ogg", Music.class);


        this.stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.skin = game.assetManager.get("main.skin", Skin.class);

        this.infoLabel = new Label(
                String.format("%s %s", MaxonConstants.GAME_NAME, MaxonConstants.GAME_VERSION),
                skin, "credits"
        );

        this.dev = new Image(game.assetManager.get("dev.png", Texture.class));
        this.whiteSquare = new Image(game.assetManager.get("sprites/white.png", Texture.class));

        infoLabel.setPosition(
                8,
                (Gdx.graphics.getHeight() - infoLabel.getHeight()) - 8
        );

        dev.setScale(5f);

        dev.setPosition(
                (Gdx.graphics.getWidth() / 2.0f) - (dev.getWidth() * 5f / 2.0f),
                (Gdx.graphics.getHeight() / 2.0f) - (dev.getHeight() * 5f / 2.0f)
        );

        whiteSquare.setPosition(0, 0);
        whiteSquare.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        dev.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.fadeIn(1f),
                Actions.delay(5f),
                Actions.fadeOut(0.25f)
        ));

        whiteSquare.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.fadeIn(0.5f),
                Actions.delay(25f),
                Actions.fadeOut(0.5f)
        ));

        stage.addActor(whiteSquare);
        stage.addActor(infoLabel);
        stage.addActor(dev);

        Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));
    }

    @Override public void show() {
        introMusic.play();
        render(Gdx.graphics.getDeltaTime());
    }

    private final float wallVelocity = 1f;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act(delta);

        if (!introMusic.isPlaying()) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() {}

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
