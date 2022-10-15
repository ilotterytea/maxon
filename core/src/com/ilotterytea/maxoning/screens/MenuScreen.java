package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ilotterytea.maxoning.MaxonConstants;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.player.MaxonSavegame;
import com.ilotterytea.maxoning.ui.*;
import com.ilotterytea.maxoning.utils.math.Math;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MenuScreen implements Screen {

    final MaxonGame game;

    final Stage stage;
    final Skin skin, widgetSkin;

    Image brandLogo, blackBg, menuBg;

    final Music menuMusic;

    Table menuTable, savegameTable;

    // Atlases:
    TextureAtlas environmentAtlas, brandAtlas;

    private ArrayList<ArrayList<Sprite>> bgMenuTiles;
    private ArrayList<LeafParticle> leafTiles, delLeafTiles;

    private final boolean isAutumn =
            // Autumn.
            ((Calendar.getInstance().get(Calendar.MONTH) + 1 > 8) && (Calendar.getInstance().get(Calendar.MONTH) + 1 < 12)) ||
            // Spring.
            ((Calendar.getInstance().get(Calendar.MONTH) + 1 < 6) && (Calendar.getInstance().get(Calendar.MONTH) + 1 > 2));
    private final boolean isSummer = (Calendar.getInstance().get(Calendar.MONTH) + 1 > 5 && Calendar.getInstance().get(Calendar.MONTH) + 1 < 9);

    public MenuScreen(final MaxonGame game) {
        this.game = game;

        // Environment atlas for leafs, snowflakes and background tiles:
        environmentAtlas = game.assetManager.get("sprites/env/environment.atlas", TextureAtlas.class);

        // Brand atlas:
        brandAtlas = game.assetManager.get("sprites/gui/brand.atlas", TextureAtlas.class);

        // Stage and skin:
        this.stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.skin = new Skin(Gdx.files.internal("main.skin"));
        this.widgetSkin = new Skin(Gdx.files.internal("sprites/gui/widgets.skin"));

        // Main Menu music:
        this.menuMusic = game.assetManager.get("mus/menu/mus_menu_loop.ogg", Music.class);

        // Make the background a little darker:
        brandLogo = new Image(brandAtlas.findRegion("brand"));
        blackBg = new Image(environmentAtlas.findRegion("tile"));

        blackBg.setColor(0f, 0f, 0f, 0.25f);
        blackBg.setSize(stage.getWidth(), stage.getHeight());

        stage.addActor(blackBg);

        // Save game table:
        savegameTable = new Table();
        loadSavegamesToTable(savegameTable);

        // Quick buttons:
        Table quickTable = new Table();
        quickTable.align(Align.right);

        // Options button:
        TextButton optionsButton = new TextButton("Options", widgetSkin, "default");
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });

        quickTable.add(optionsButton).height(48f).minWidth(90f).pad(4f);

        // Quit button:
        TextButton quitButton = new TextButton("Quit", widgetSkin, "default");
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        quickTable.add(quitButton).height(48f).minWidth(90f).pad(4f);

        // Menu table:
        menuTable = new Table();
        menuTable.setPosition(0, 0);
        menuTable.setSize(stage.getWidth(), stage.getHeight());
        menuTable.align(Align.center);

        Label menuTitle = new Label("Please select a save slot", skin, "default");
        menuTitle.setAlignment(Align.left);

        menuTable.add(menuTitle).width(512f).row();
        menuTable.add(savegameTable).width(512f).maxWidth(640f).row();
        menuTable.add(quickTable).width(512f);

        stage.addActor(menuTable);

        // // Logo:
        brandLogo = new Image(brandAtlas.findRegion("brand"));
        brandLogo.setPosition(
                (stage.getWidth() / 2f) - (brandLogo.getWidth() / 2f),
                stage.getHeight() - brandLogo.getHeight() * 1.5f
        );

        brandLogo.setOrigin(
                brandLogo.getWidth() / 2f,
                brandLogo.getHeight() / 2f
        );

        brandLogo.addAction(
                Actions.repeat(
                        RepeatAction.FOREVER,
                        Actions.sequence(
                                Actions.parallel(
                                        Actions.rotateTo(-5f, 5f, Interpolation.smoother),
                                        Actions.scaleTo(0.9f, 0.9f, 5f, Interpolation.smoother)
                                ),
                                Actions.parallel(
                                        Actions.rotateTo(5f, 5f, Interpolation.smoother),
                                        Actions.scaleTo(1.1f, 1.1f, 5f, Interpolation.smoother)
                                )
                        )
                )
        );

        stage.addActor(brandLogo);

        // Debug label:
        DebugLabel debug = new DebugLabel(skin);
        debug.setPosition(4, stage.getHeight() - debug.getHeight() - 4);
        stage.addActor(debug);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage));

        // Generate background tiles:
        bgMenuTiles = new ArrayList<>();

        genNewBgTiles((int) stage.getWidth(), (int) stage.getHeight());
        leafTiles = new ArrayList<>();
        delLeafTiles = new ArrayList<>();
    }

    @Override public void show() {
        // Start to render:
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Generate a new leaf:
        if (!isSummer) {
            LeafParticle _leaf = new LeafParticle(
                    (isAutumn) ? environmentAtlas.findRegion("leaf") : environmentAtlas.findRegion("snowflake"),
                    (float) java.lang.Math.floor(java.lang.Math.random() * Gdx.graphics.getWidth()),
                    Gdx.graphics.getHeight(),
                    (float) Math.getRandomNumber(-257, 256) + 1,
                    (float) Math.getRandomNumber(-257, 256) + 1,
                    (float) Math.getRandomNumber(5, 15));

            _leaf.setScale(5f);

            if (isAutumn) {
                switch (Math.getRandomNumber(0, 3)) {
                    case 0: _leaf.setColor(Color.CORAL); break;
                    case 1: _leaf.setColor(Color.YELLOW); break;
                    default: _leaf.setColor(Color.RED); break;
                }
            } else {
                switch (Math.getRandomNumber(0, 1)) {
                    case 0: _leaf.setColor(Color.WHITE); break;
                    case 1: _leaf.setColor(Color.SKY);
                }
            }

            leafTiles.add(_leaf);
        }

        game.batch.begin();

        for (ArrayList<Sprite> array : bgMenuTiles) {
            for (Sprite spr : array) {
                spr.setPosition(spr.getX() + 1, spr.getY());
                spr.draw(game.batch);
            }
        }

        for (LeafParticle spr : leafTiles) {
            spr.draw(game.batch);
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

        if (!isSummer) {
            for (LeafParticle spr : leafTiles) {
                if (spr.getX() > Gdx.graphics.getWidth() || spr.getY() > Gdx.graphics.getHeight()) {
                    delLeafTiles.add(spr);
                }
            }

            for (LeafParticle spr : delLeafTiles) { leafTiles.remove(spr); }
            delLeafTiles.clear();
        }

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        bgMenuTiles.clear();

        genNewBgTiles(width, height);

        stage.getViewport().update(width, height, true);
    }

    private void loadSavegamesToTable(Table table) {
        for (int i = 0; i < 3; i++) {
            if (new File(MaxonConstants.GAME_SAVEGAME_FOLDER + String.format("/0%s.maxon", i)).exists()) {
                final MaxonSavegame sav = GameDataSystem.load("0" + i + ".maxon");
                SaveGameWidget widget = new SaveGameWidget(
                        skin, widgetSkin, sav
                );
                final int finalI = i;
                widget.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        try {
                            game.setScreen(new GameScreen(game, sav, finalI));
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        dispose();
                    }
                });
                table.add(widget).width(512f).padBottom(8f).row();
            } else {

                final MaxonSavegame sav = new MaxonSavegame();
                final SaveGameWidget widget = new SaveGameWidget(
                        skin, widgetSkin, null
                );
                final int finalI = i;
                widget.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        sav.petId = 0;
                        sav.inv = new ArrayList<>();
                        sav.multiplier = 5;
                        sav.points = 0;
                        sav.roomId = 0;
                        sav.seed = System.currentTimeMillis();
                        sav.name = "SAVE " + (finalI + 1);
                        sav.elapsedTime = 0;
                        sav.lastTimestamp = System.currentTimeMillis();
                        sav.outInv = new ArrayList<>();

                        GameDataSystem.save(sav, "0" + finalI + ".maxon");

                        try {
                            game.setScreen(new GameScreen(game, sav, finalI));
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        dispose();
                    }
                });
                table.add(widget).width(512f).padBottom(8f).row();
            }
        }
    }

    private void genNewBgTiles(int width, int height) {
        bgMenuTiles.clear();

        for (int i = 0; i < height / environmentAtlas.findRegion("tile").getRegionHeight() + 1; i++) {
            bgMenuTiles.add(i, new ArrayList<Sprite>());
            for (int j = -1; j < width / environmentAtlas.findRegion("tile").getRegionWidth(); j++) {
                Sprite spr = new Sprite(environmentAtlas.findRegion("tile"));

                if ((j + i) % 2 == 0) {
                    spr.setColor(0.98f, 0.71f, 0.22f, 1f);
                } else {
                    spr.setColor(0.84f, 0.61f, 0.20f, 1f);
                }

                spr.setSize(64, 64);

                spr.setPosition(spr.getWidth() * j, spr.getHeight() * i);
                bgMenuTiles.get(i).add(spr);
            }
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() {
        stage.clear();
        skin.dispose();
    }
}
