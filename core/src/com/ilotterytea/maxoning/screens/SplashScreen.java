package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.inputprocessors.CrossProcessor;
import com.ilotterytea.maxoning.ui.DebugLabel;

public class SplashScreen implements InputProcessor, Screen {

    final MaxonGame game;

    final Stage stage;
    final Skin skin;

    final Image whiteSquare, dev, org;
    final Label infoLabel, disclaimer, legalLabel;

    final Music introMusic;

    public SplashScreen(MaxonGame game) {
        this.game = game;

        this.introMusic = game.assetManager.get("mus/menu/mus_menu_intro.ogg", Music.class);

        this.stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.skin = new Skin(Gdx.files.internal("main.skin"));

        this.infoLabel = new DebugLabel(skin);
        this.disclaimer = new Label(game.locale.TranslatableText("splash.disclaimer"), skin, "disclaimer");
        this.legalLabel = new Label("", skin, "disclaimer");

        this.dev = new Image(game.assetManager.get("sprites/ilotterytea.png", Texture.class));
        this.org = new Image(game.assetManager.get("sprites/supadank.png", Texture.class));
        this.whiteSquare = new Image(game.assetManager.get("sprites/white.png", Texture.class));

        disclaimer.setBounds(0, 0, 800, 600);

        infoLabel.setPosition(
                8,
                (Gdx.graphics.getHeight() - infoLabel.getHeight()) - 8
        );

        dev.setScale(5f);

        dev.setPosition(
                (Gdx.graphics.getWidth() / 2.0f) - (dev.getWidth() * 5f / 2.0f),
                (Gdx.graphics.getHeight() / 2.0f) - (dev.getHeight() * 5f / 2.0f)
        );

        org.setPosition(
                (Gdx.graphics.getWidth() / 2.0f) - (org.getWidth() / 2.0f),
                (Gdx.graphics.getHeight() / 2.0f) - (org.getHeight() / 2.0f)
        );

        disclaimer.setPosition(
                (Gdx.graphics.getWidth() / 2.0f) - (800 / 2.0f),
                (Gdx.graphics.getHeight() / 2.0f) - (600 / 2.0f)
        );

        whiteSquare.setPosition(0, 0);
        whiteSquare.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        dev.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.fadeIn(1f),
                Actions.delay(5f),
                Actions.fadeOut(0.25f)
        ));

        org.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.delay(7.3f),
                Actions.fadeIn(2.5f),
                Actions.delay(5f),
                Actions.fadeOut(5f)
        ));

        disclaimer.addAction(
                Actions.sequence(
                        Actions.alpha(0f),
                        Actions.delay(19.8f),
                        Actions.fadeIn(1f),
                        Actions.delay(3f),
                        Actions.fadeOut(1f)
                )
        );

        whiteSquare.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.fadeIn(0.5f),
                Actions.delay(25f),
                Actions.fadeOut(0.5f)
        ));
        disclaimer.setWrap(true);

        stage.addActor(whiteSquare);
        stage.addActor(infoLabel);
        stage.addActor(dev);
        stage.addActor(org);
        stage.addActor(disclaimer);

        Gdx.input.setInputProcessor(new InputMultiplexer(this, new CrossProcessor(), stage));
    }

    @Override public void show() {
        introMusic.setVolume(game.prefs.getFloat("music", 0.5f));
        introMusic.play();
        render(Gdx.graphics.getDeltaTime());
    }

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
            introMusic.stop();
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
