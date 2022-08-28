package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ilotterytea.maxoning.MaxonConstants;
import com.ilotterytea.maxoning.MaxonGame;

import java.util.ArrayList;

public class MenuScreen implements Screen, InputProcessor {

    final MaxonGame game;

    final Stage stage;
    final Skin skin;

    final Image brandLogo, maxonLogo;
    final Label startLabel, infoLabel;

    final Texture wall, brandTxr, maxonTxr;

    final Music menuMusic;

    private ArrayList<ArrayList<Sprite>> wallTiles = new ArrayList<>();

    private boolean anyKeyPressed = false, brandActionsSet = false;

    public MenuScreen(MaxonGame game) {
        this.game = game;

        this.stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.skin = new Skin(Gdx.files.internal("main.skin"));

        this.brandTxr = new Texture(Gdx.files.internal("sprites/brand.png"));
        this.maxonTxr = new Texture(Gdx.files.internal("icon.png"));
        this.wall = new Texture(Gdx.files.internal("sprites/SplashWall.png"));

        this.menuMusic = game.assetManager.get("mus/menu/mus_menu_loop.ogg", Music.class);

        for (int i = 0; i < (Gdx.graphics.getHeight() / wall.getHeight()) + 1; i++) {
            wallTiles.add(new ArrayList<Sprite>());
            for (int j = 0; j < (Gdx.graphics.getWidth() / wall.getWidth()); j++) {
                Sprite spr = new Sprite(wall);
                spr.setPosition(wall.getWidth() * j, wall.getHeight() * i);

                wallTiles.get(i).add(spr);
            }
        }

        this.maxonLogo = new Image(maxonTxr);
        this.brandLogo = new Image(brandTxr);

        this.startLabel = new Label("PRESS START", skin, "press");
        this.infoLabel = new Label(String.format("%s %s", MaxonConstants.GAME_NAME, MaxonConstants.GAME_VERSION), skin, "credits");

        brandLogo.setScale(100f);

        brandLogo.setPosition(
                (Gdx.graphics.getWidth() / 2.0f) - (brandLogo.getWidth() / 2.0f),
                (Gdx.graphics.getHeight() / 2.0f) - (brandLogo.getHeight() / 2.0f)
        );

        brandLogo.setOrigin(
                brandLogo.getWidth() / 2.0f,
                brandLogo.getHeight() / 2.0f
        );

        brandLogo.addAction(
                Actions.sequence(
                        Actions.alpha(0),
                        Actions.parallel(
                                Actions.fadeIn(1f),
                                Actions.scaleTo(1f, 1f, 1f, Interpolation.pow2InInverse)
                        ),
                        Actions.repeat(
                                RepeatAction.FOREVER,
                                Actions.sequence(
                                        Actions.parallel(
                                                Actions.rotateTo(-10, 10f, Interpolation.sine),
                                                Actions.scaleTo(0.85f, 0.85f, 10f, Interpolation.sine)
                                        ),
                                        Actions.parallel(
                                                Actions.rotateTo(10, 10f, Interpolation.sine),
                                                Actions.scaleTo(1.25f, 1.25f, 10f, Interpolation.sine)
                                        )
                                )
                        ))
        );

        startLabel.setPosition(
                (Gdx.graphics.getWidth() / 2.0f) - (startLabel.getWidth() / 2.0f),
                32
        );

        startLabel.addAction(
                Actions.repeat(
                        RepeatAction.FOREVER,
                        Actions.sequence(
                                Actions.alpha(0f),
                                Actions.fadeIn(1f),
                                Actions.delay(5f),
                                Actions.fadeOut(1f)
                        )
                )
        );

        stage.addActor(infoLabel);
        stage.addActor(brandLogo);
        stage.addActor(startLabel);

        Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));
    }

    @Override public void show() {
        render(Gdx.graphics.getDeltaTime());
        menuMusic.setLooping(true);
        menuMusic.play();
    }

    private final float wallVelocity = 1f;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the sprites:
        /*for (ArrayList<Sprite> sprArray : wallTiles) {
            for (Sprite tile : sprArray) {
                tile.setPosition(tile.getX() + wallVelocity, tile.getY());
                tile.draw(game.batch);
            }
        }*/

        /*
        for (ArrayList<Sprite> sprArray : wallTiles) {
            for (int i = 0; i < sprArray.size(); i++) {
                Sprite spr = sprArray.get(i);

                if (spr.getX() + spr.getWidth() > Gdx.graphics.getWidth()) {
                    spr.setPosition(sprArray.get(0).getX() - spr.getWidth(), spr.getY());
                    sprArray.remove(i);
                    sprArray.add(0, spr);
                }
            }
        }*/

        if (anyKeyPressed && !brandActionsSet) {
            startLabel.clearActions();
            startLabel.addAction(Actions.fadeOut(0.5f));

            brandLogo.clearActions();
            brandLogo.addAction(
                    Actions.sequence(
                            Actions.parallel(
                                Actions.rotateTo(0f, 2f),
                                Actions.scaleTo(1f, 1f, 2f),
                                Actions.moveTo(
                                        (Gdx.graphics.getWidth() / 2f) - (brandTxr.getWidth() / 2f),
                                        (Gdx.graphics.getHeight() - brandTxr.getHeight()) - 81,
                                        2f,
                                        Interpolation.sine
                                )
                            ),
                            Actions.repeat(RepeatAction.FOREVER,
                                    Actions.sequence(
                                            Actions.parallel(
                                                    Actions.rotateTo(-5f, 5f, Interpolation.smoother),
                                                    Actions.scaleTo(0.9f, 0.9f, 5f, Interpolation.smoother)
                                            ),
                                            Actions.parallel(
                                                    Actions.rotateTo(5f, 5f, Interpolation.smoother),
                                                    Actions.scaleTo(1.1f, 1.1f, 5f, Interpolation.smoother)
                                            )
                                    )))
            );

            brandActionsSet = true;
        }

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            anyKeyPressed = true;
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
        if (Gdx.input.isTouched()) {
            anyKeyPressed = true;
        }
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
