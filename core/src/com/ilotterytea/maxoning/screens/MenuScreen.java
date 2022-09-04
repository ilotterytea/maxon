package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.inputprocessors.CrossProcessor;
import com.ilotterytea.maxoning.ui.*;

import java.io.IOException;
import java.util.ArrayList;

public class MenuScreen implements Screen, InputProcessor {

    final MaxonGame game;

    final Stage stage;
    final Skin skin;

    Image brandLogo, blackBg;
    Label startLabel, infoLabel;

    NinepatchButton singlePlayerButton, optionsButton, quitButton;
    final Music menuMusic;

    Table menuTable, optionsTable;

    final Texture bgTile1, bgTile2;

    NinePatch buttonUp, buttonDown, buttonOver, buttonDisabled;

    private ArrayList<ArrayList<Sprite>> bgMenuTiles;

    private boolean anyKeyPressed = false, brandActionsSet = false;

    public MenuScreen(final MaxonGame game) {
        this.game = game;

        buttonUp = new NinePatch(game.assetManager.get("sprites/ui/sqrbutton.png", Texture.class), 8, 8, 8, 8);
        buttonDown = new NinePatch(game.assetManager.get("sprites/ui/sqrbutton_down.png", Texture.class), 8, 8, 8, 8);
        buttonOver = new NinePatch(game.assetManager.get("sprites/ui/sqrbutton_over.png", Texture.class), 8, 8, 8, 8);
        buttonDisabled = new NinePatch(game.assetManager.get("sprites/ui/sqrbutton_disabled.png", Texture.class), 8, 8, 8, 8);

        bgTile1 = game.assetManager.get("sprites/menu/tile_1.png", Texture.class);
        bgTile2 = game.assetManager.get("sprites/menu/tile_2.png", Texture.class);

        bgMenuTiles = new ArrayList<>();

        for (int i = 0; i < Gdx.graphics.getHeight() / bgTile1.getHeight() + 1; i++) {
            bgMenuTiles.add(i, new ArrayList<Sprite>());
            for (int j = -1; j < Gdx.graphics.getWidth() / bgTile1.getWidth(); j++) {
                Sprite spr = new Sprite((j + i % 2 == 0) ? bgTile1 : bgTile2);

                spr.setPosition(bgTile1.getWidth() * j, bgTile1.getHeight() * i);
                bgMenuTiles.get(i).add(spr);
            }
        }

        this.stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.skin = new Skin(Gdx.files.internal("main.skin"));

        this.menuMusic = game.assetManager.get("mus/menu/mus_menu_loop.ogg", Music.class);

        brandLogo = new Image(game.assetManager.get("sprites/brand.png", Texture.class));
        blackBg = new Image(game.assetManager.get("sprites/black.png", Texture.class));

        this.startLabel = new Label(game.locale.TranslatableText("menu.pressStart"), skin, "press");
        this.infoLabel = new DebugLabel(skin);

        // Menu Buttons:
        menuTable = new Table();

        singlePlayerButton = new NinepatchButton(buttonUp, buttonDown, buttonOver, game.locale.TranslatableText("menu.playGame"), skin, "default");
        optionsButton = new NinepatchButton(buttonUp, buttonDown, buttonOver, game.locale.TranslatableText("menu.options"), skin, "default");
        quitButton = new NinepatchButton(buttonUp, buttonDown, buttonOver, game.locale.TranslatableText("menu.quit"), skin, "default");

        singlePlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    game.setScreen(new GameScreen(game));
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                dispose();
            }
        });

        // Options:
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuTable.clearActions();
                menuTable.addAction(Actions.moveTo(-Gdx.graphics.getWidth(), menuTable.getY(), 0.75f, Interpolation.sine));

                optionsTable.clearActions();
                optionsTable.addAction(Actions.moveTo(0, optionsTable.getY(), 0.75f, Interpolation.sine));

                blackBg.clearActions();
                blackBg.addAction(Actions.alpha(0.5f));

                brandLogo.addAction(
                        Actions.moveTo(brandLogo.getX(), brandLogo.getY() + 512f, 0.5f, Interpolation.sine)
                );
            }
        });

        // Exit the game when "quit button" is pressed:
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Set the width and position for menu table:
        menuTable.setPosition(0, Gdx.graphics.getHeight());
        menuTable.setWidth(Gdx.graphics.getWidth());
        menuTable.align(Align.center);

        menuTable.add(singlePlayerButton).width(512f).height(81f).padBottom(10f).row();
        menuTable.add(optionsButton).width(512f).height(81f).padBottom(91f).row();
        menuTable.add(quitButton).width(512f).height(81f).row();

        blackBg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        blackBg.addAction(Actions.alpha(0.25f));

        // Options table:
        optionsTable = new OptionsTable(game, skin, buttonUp, buttonDown, buttonOver, menuMusic, menuTable, blackBg, brandLogo);

        stage.addActor(blackBg);
        stage.addActor(infoLabel);
        stage.addActor(brandLogo);
        stage.addActor(startLabel);
        stage.addActor(menuTable);
        stage.addActor(optionsTable);

        menuTable.addAction(Actions.sequence(Actions.alpha(0f), Actions.moveTo(0f, -Gdx.graphics.getHeight() - Gdx.graphics.getHeight(), 0f)));
        optionsTable.addAction(Actions.moveTo(Gdx.graphics.getWidth(), 0, 0f));

        Gdx.input.setInputProcessor(new InputMultiplexer(this, new CrossProcessor(), stage));

        // Setting the music:
        if (game.prefs.getBoolean("music", true)) {
            menuMusic.setLooping(true);
            menuMusic.setVolume((game.prefs.getBoolean("music", true)) ? 1f : 0f);
            menuMusic.play();
        }
    }

    @Override public void show() {
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

        infoLabel.setPosition(8, (Gdx.graphics.getHeight() - infoLabel.getHeight() - 8));

        // Start to render:
        render(Gdx.graphics.getDeltaTime());


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (anyKeyPressed && !brandActionsSet) {
            startLabel.clearActions();
            startLabel.addAction(Actions.fadeOut(0.5f));

            brandLogo.clearActions();
            brandLogo.addAction(
                    Actions.sequence(
                            Actions.parallel(
                                    Actions.alpha(1f),
                                Actions.rotateTo(0f, 2f),
                                Actions.scaleTo(1f, 1f, 2f),
                                Actions.moveTo(
                                        (Gdx.graphics.getWidth() / 2f) - (brandLogo.getWidth() / 2f),
                                        (Gdx.graphics.getHeight() - brandLogo.getHeight()) - 81,
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

            menuTable.clearActions();
            menuTable.addAction(
                    Actions.parallel(
                            Actions.fadeIn(1.5f),
                            Actions.moveTo(0, (Gdx.graphics.getHeight() / 2f) - (menuTable.getHeight() / 2f) - 64f, 2.5f, Interpolation.smoother)
                    )
            );

            brandActionsSet = true;
        }

        game.batch.begin();

        for (ArrayList<Sprite> array : bgMenuTiles) {
            for (Sprite spr : array) {
                spr.setPosition(spr.getX() + 1, spr.getY());
                spr.draw(game.batch);
            }
        }

        game.batch.end();

        for (ArrayList<Sprite> array : bgMenuTiles) {
            for (int i = 0; i < array.size(); i++) {
                Sprite spr = array.get(i);
                Sprite f_spr = array.get(0);

                if (spr.getX() > Gdx.graphics.getWidth()) {
                    Sprite n_spr = spr;
                    n_spr.setPosition(f_spr.getX() - spr.getWidth(), f_spr.getY());

                    if (spr.getTexture() == f_spr.getTexture()) {
                        n_spr.setTexture(array.get(1).getTexture());
                    }

                    array.remove(spr);
                    array.add(0, n_spr);
                }
            }
        }

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        bgMenuTiles.clear();

        for (int i = 0; i < height / bgTile1.getHeight() + 1; i++) {
            bgMenuTiles.add(i, new ArrayList<Sprite>());
            for (int j = -1; j < width / bgTile1.getWidth(); j++) {
                Sprite spr = new Sprite();

                if ((j + i) % 2 == 0) {
                    spr.setTexture(bgTile1);
                } else {
                    spr.setTexture(bgTile2);
                }

                spr.setSize(bgTile1.getWidth(), bgTile1.getHeight());
                spr.setPosition(bgTile1.getWidth() * j, bgTile1.getHeight() * i);
                bgMenuTiles.get(i).add(spr);
            }
        }

        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() {
        stage.clear();
        skin.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
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
        if (!anyKeyPressed) {
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
