package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ilotterytea.maxoning.MaxonConstants;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.player.MaxonSavegame;
import com.ilotterytea.maxoning.ui.*;
import com.ilotterytea.maxoning.utils.I18N;
import com.ilotterytea.maxoning.utils.formatters.NumberFormatter;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MenuScreen implements Screen {

    final MaxonGame game;

    final Stage stage;
    final Skin skin;
    TextureAtlas brandAtlas;

    Image brandLogo;

    final Music menuMusic;

    Table menuTable;

    TextButton startBtn;
    Label savLabel;


    MaxonSavegame sav;

    private final MovingChessBackground bg;

    public MenuScreen(final MaxonGame game) {
        this.game = game;

        // Stage and skin:
        this.stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.skin = game.assetManager.get("MainSpritesheet.skin", Skin.class);
        brandAtlas = game.assetManager.get("sprites/gui/brand.atlas", TextureAtlas.class);

        sav = GameDataSystem.load("00.maxon");

        // Main Menu music:
        this.menuMusic = game.assetManager.get("mus/menu/mus_menu_loop.ogg", Music.class);

        // // Menu table:
        float iconSize = 64f, iconPad = 6f;
        menuTable = new Table();
        menuTable.setSize(stage.getWidth() / 2f, iconSize);
        menuTable.setPosition(0, 0);
        menuTable.pad(iconPad);
        menuTable.align(Align.bottomLeft);

        // Quit button:
        ImageButton quitBtn = new ImageButton(skin, "quit");

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        menuTable.add(quitBtn).size(iconSize).pad(iconPad);

        // Options button:
        ImageButton optBtn = new ImageButton(skin, "options");

        optBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showOptions();
            }
        });

        menuTable.add(optBtn).size(iconSize).pad(iconPad);

        stage.addActor(menuTable);

        // // Press start:
        startBtn = new TextButton(game.locale.TranslatableText("menu.pressStart"), skin, "text");
        startBtn.setPosition((stage.getWidth() / 2f) - (startBtn.getWidth() / 2f), 8f);

        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    game.setScreen(new GameScreen(
                            game,
                            (sav == null) ? new MaxonSavegame() : sav,
                            0
                    ));
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        startBtn.addAction(
                Actions.repeat(
                        -1,
                        Actions.sequence(
                                Actions.fadeIn(1f),
                                Actions.delay(2f),
                                Actions.fadeOut(1f),
                                Actions.delay(2f)
                        )
                )
        );

        stage.addActor(startBtn);

        // // Savegame:
        savLabel = new Label(
                (sav == null) ? game.locale.TranslatableText("menu.last_savegame.empty") : game.locale.FormattedText("menu.last_savegame.found", sav.petName, NumberFormatter.format(sav.points), NumberFormatter.format(sav.multiplier), String.valueOf(sav.inv.size())), skin);
        savLabel.setPosition((stage.getWidth() / 2f) - (savLabel.getWidth() / 2f), 8f + startBtn.getY() + startBtn.getHeight());

        stage.addActor(savLabel);

        // // Logo:
        brandLogo = new Image(brandAtlas.findRegion("brand"));
        brandLogo.setPosition(
                (stage.getWidth() / 2f) - (brandLogo.getWidth() / 2f),
                (stage.getHeight() / 2f) - (brandLogo.getHeight() / 2f)
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
        this.bg = new MovingChessBackground(
                1,
                1,
                stage.getWidth(),
                stage.getHeight(),
                skin.getDrawable("tile_01"),
                skin.getDrawable("tile_02")
        );
    }

    @Override public void show() {
        // Start to render:
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        bg.draw(game.batch);

        game.batch.end();

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        bg.update(width, height);

        stage.getViewport().update(width, height, true);
    }

    private void showOptions() {
        startBtn.addAction(Actions.moveTo(startBtn.getX(), -startBtn.getY() - startBtn.getHeight(), 1f, Interpolation.exp10Out));
        savLabel.addAction(Actions.moveTo(savLabel.getX(), -savLabel.getY() - savLabel.getHeight(), 1f, Interpolation.exp10Out));
        menuTable.addAction(Actions.moveTo(menuTable.getX(), -menuTable.getY() - menuTable.getHeight() - 48f, 1f, Interpolation.exp10Out));

        brandLogo.clearActions();
        brandLogo.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.moveTo(
                                        (stage.getWidth() / 2f) - (brandLogo.getWidth() / 2f),
                                        stage.getHeight() - brandLogo.getHeight() * 1.5f,
                                        1f,
                                        Interpolation.fade
                                ),
                                Actions.rotateTo(0f, .25f, Interpolation.fade)
                        ),
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
                )
        );

        // Main options window:
        final Table mOptTable = new Table();
        mOptTable.setPosition(0, 0);
        mOptTable.align(Align.center);
        mOptTable.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(mOptTable);

        // Options title:
        Label optTitle = new Label(game.locale.TranslatableText("options.title"), skin);
        optTitle.setAlignment(Align.left);
        mOptTable.add(optTitle).width(512f).row();

        // Options table:
        Table optTable = new Table(skin);
        optTable.setBackground("bg");
        optTable.align(Align.topLeft);

        // Scroll panel for options:
        ScrollPane optScroll = new ScrollPane(optTable);
        optScroll.setScrollingDisabled(true, false);
        mOptTable.add(optScroll).width(512f).height(384f).row();

        // - - -  General category  - - -:
        Label genLabel = new Label(game.locale.TranslatableText("options.general"), skin);
        optTable.add(genLabel).expandX().row();

        Table genCategory = new Table();
        optTable.add(genCategory).expandX().row();

        // Show debug:
        Label debLabel = new Label(game.locale.TranslatableText("options.debug"), skin);
        debLabel.setAlignment(Align.left);
        genCategory.add(debLabel).width(256f);

        final TextButton debButton = new TextButton((game.prefs.getBoolean("debug", false)) ? "ON" : "OFF", skin);

        debButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("debug", false);

                game.prefs.putBoolean("debug", !value);
                game.prefs.flush();

                value = !value;

                debButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        genCategory.add(debButton).width(256f).row();

        // - - -  Audio category  - - -:
        Label audioLabel = new Label(game.locale.TranslatableText("options.audio"), skin);
        optTable.add(audioLabel).expandX().row();

        Table audioCategory = new Table();
        optTable.add(audioCategory).expandX().row();

        // Music:
        Label musLabel = new Label(game.locale.TranslatableText("options.music"), skin);
        musLabel.setAlignment(Align.left);
        audioCategory.add(musLabel).width(256f);

        final TextButton musButton = new TextButton((game.prefs.getBoolean("music", true)) ? "ON" : "OFF", skin);

        musButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("music", true);

                game.prefs.putBoolean("music", !value);
                game.prefs.flush();

                value = !value;

                if (value) menuMusic.play();
                else menuMusic.pause();

                musButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        audioCategory.add(musButton).width(256f).row();

        // Sound:
        Label sndLabel = new Label(game.locale.TranslatableText("options.sound"), skin);
        sndLabel.setAlignment(Align.left);
        audioCategory.add(sndLabel).width(256f);

        final TextButton sndButton = new TextButton((game.prefs.getBoolean("sfx", true)) ? "ON" : "OFF", skin);

        sndButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("sfx", true);

                game.prefs.putBoolean("sfx", !value);
                game.prefs.flush();

                value = !value;

                sndButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        audioCategory.add(sndButton).width(256f).row();

        // - - -  Video category  - - -:
        Label videoLabel = new Label(game.locale.TranslatableText("options.video"), skin);
        optTable.add(videoLabel).expandX().row();

        Table videoCategory = new Table();
        optTable.add(videoCategory).expandX().row();

        // Vertical sync:
        Label vscLabel = new Label(game.locale.TranslatableText("options.vsync"), skin);
        vscLabel.setAlignment(Align.left);
        videoCategory.add(vscLabel).width(256f);

        final TextButton vscButton = new TextButton((game.prefs.getBoolean("vsync", true)) ? "ON" : "OFF", skin);

        vscButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("vsync", true);

                game.prefs.putBoolean("vsync", !value);
                game.prefs.flush();

                value = !value;

                Gdx.graphics.setVSync(value);

                vscButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        videoCategory.add(vscButton).width(256f).row();

        // Full screen:
        Label fscLabel = new Label(game.locale.TranslatableText("options.fullscreen"), skin);
        fscLabel.setAlignment(Align.left);
        videoCategory.add(fscLabel).width(256f);

        final TextButton fscButton = new TextButton((game.prefs.getBoolean("fullscreen", true)) ? "ON" : "OFF", skin);

        fscButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("fullscreen", true);

                game.prefs.putBoolean("fullscreen", !value);
                game.prefs.flush();

                value = !value;
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

                if (value) Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                else Gdx.graphics.setWindowedMode(dim.width, dim.height);

                fscButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        videoCategory.add(fscButton).width(256f).row();

        // - - -  Switch the language  - - -:
        String[] fh4Locale = game.locale.getFileHandle().nameWithoutExtension().split("_");
        Locale locale = new Locale(fh4Locale[0], fh4Locale[1]);
        final TextButton langButton = new TextButton(game.locale.FormattedText("options.language", locale.getDisplayLanguage(), locale.getDisplayCountry()), skin);

        langButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int index = 0;
                ArrayList<FileHandle> fhArray = new ArrayList<>();
                fhArray.add(MaxonConstants.FILE_RU_RU);
                fhArray.add(MaxonConstants.FILE_EN_US);

                if (fhArray.indexOf(game.locale.getFileHandle()) + 1 < fhArray.size()) {
                    index = fhArray.indexOf(game.locale.getFileHandle()) + 1;
                }

                FileHandle fhNext = fhArray.get(index);

                game.locale = new I18N(fhNext);
                game.prefs.putString("lang", fhNext.nameWithoutExtension());
                game.prefs.flush();

                String[] fh4Locale = fhNext.nameWithoutExtension().split("_");
                Locale locale = new Locale(fh4Locale[0], fh4Locale[1]);

                langButton.setText(game.locale.FormattedText("options.language", locale.getDisplayLanguage(), locale.getDisplayCountry()));
                game.setScreen(new SplashScreen(game));
                menuMusic.stop();
            }
        });

        optTable.add(langButton).width(512f).row();

        // - - -  Reset save data  - - -:
        TextButton resetButton = new TextButton(game.locale.TranslatableText("options.reset"), skin);
        optTable.add(resetButton).width(512f).row();

        // Game info:
        Label infLabel = new Label(String.format("%s - %s", MaxonConstants.GAME_NAME, MaxonConstants.GAME_VERSION), skin);
        infLabel.setAlignment(Align.center);
        optTable.add(infLabel).maxWidth(512f).row();

        // // Action buttons:
        Table actTable = new Table(skin);
        actTable.setBackground("bg");
        actTable.setWidth(512f);
        actTable.align(Align.right);
        mOptTable.add(actTable).width(512f).maxWidth(512f).pad(5f);

        TextButton closeBtn = new TextButton("Back to main menu", skin);

        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mOptTable.remove();

                brandLogo.clearActions();
                brandLogo.addAction(
                        Actions.sequence(
                                Actions.parallel(
                                        Actions.rotateTo(0f, 1f),
                                        Actions.moveTo(
                                                (stage.getWidth() / 2f) - (brandLogo.getWidth() / 2f),
                                                (stage.getHeight() / 2f) - (brandLogo.getHeight() / 2f),
                                                1f,
                                                Interpolation.fade
                                        )
                                ),
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
                        )
                );

                startBtn.addAction(Actions.moveTo(startBtn.getX(), 8f, 1f, Interpolation.smoother));
                savLabel.addAction(Actions.moveTo(savLabel.getX(), 16f + startBtn.getHeight(), 1f, Interpolation.smoother));
                menuTable.addAction(Actions.moveTo(menuTable.getX(), 0, 1f, Interpolation.smoother));
            }
        });

        actTable.add(closeBtn).pad(5f);

        TextButton saveBtn = new TextButton("Apply", skin);
        actTable.add(saveBtn).pad(5f);
    }

    /*private void loadSavegamesToTable(Table table) {
        for (int i = 0; i < 3; i++) {
            if (new File(MaxonConstants.GAME_SAVEGAME_FOLDER + String.format("/0%s.maxon", i)).exists()) {
                final MaxonSavegame sav = GameDataSystem.load("0" + i + ".maxon");
                SaveGameWidget widget = new SaveGameWidget(
                        skin, sav
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
    }*/

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() {
        stage.dispose();
    }
}
