package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.ilotterytea.maxoning.anim.SpriteUtils;
import com.ilotterytea.maxoning.player.MaxonSavegame;
import com.ilotterytea.maxoning.player.utils.PetUtils;
import com.ilotterytea.maxoning.ui.*;
import com.ilotterytea.maxoning.utils.I18N;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class MenuScreen implements Screen {

    final MaxonGame game;

    final Stage stage;
    final Skin skin;
    TextureAtlas brandAtlas, mainAtlas;

    Image brandLogo;

    final Music menuMusic;

    Table menuTable;

    TextButton startBtn;
    ImageButton rArrowBtn, lArrowBtn;
    Label savLabel;
    final DebugInfo debugInfo;


    MaxonSavegame sav;

    ArrayList<SavegameInfo> savInfos;
    ArrayList<Image> savImgs;

    int curSav;
    SavegameInfo curSavInfo;
    Image curSavImg;

    boolean inOptions = false;

    private final MovingChessBackground bg;

    public MenuScreen(final MaxonGame game) {
        this.game = game;

        savInfos = new ArrayList<>();
        savImgs = new ArrayList<>();
        curSav = -1;

        // Stage and skin:
        this.stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        this.skin = game.assetManager.get("MainSpritesheet.skin", Skin.class);
        brandAtlas = game.assetManager.get("sprites/gui/brand.atlas", TextureAtlas.class);
        mainAtlas = game.assetManager.get("MainSpritesheet.atlas", TextureAtlas.class);

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

        // // Logo:
        brandLogo = new Image(brandAtlas.findRegion("brand"));
        brandLogo.setPosition(
                (stage.getWidth() / 2f) - (brandLogo.getWidth() / 2f),
                stage.getHeight() - brandLogo.getHeight() * 1.2f
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

        // Debug info:
        debugInfo = new DebugInfo(skin, game.locale);
        debugInfo.setPosition(4, (stage.getHeight() / 2f) + 128f);
        if (game.prefs.getBoolean("debug")) stage.addActor(debugInfo);

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

        generateSaves();

        updateCurrentVisualSavegame(false);

        // Save control buttons:
        rArrowBtn = new ImageButton(skin, "right_arrow");
        rArrowBtn.setPosition(
                stage.getWidth() - (rArrowBtn.getWidth() * 2),
                (stage.getHeight() / 2f) - (rArrowBtn.getHeight() / 2f)
        );
        rArrowBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!inOptions) updateCurrentVisualSavegame(false);
            }
        });

        stage.addActor(rArrowBtn);


        // Save control buttons:
        lArrowBtn = new ImageButton(skin, "left_arrow");
        lArrowBtn.setPosition(
                lArrowBtn.getWidth(),
                (stage.getHeight() / 2f) - (lArrowBtn.getHeight() / 2f)
        );
        lArrowBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!inOptions) updateCurrentVisualSavegame(true);
            }
        });

        stage.addActor(lArrowBtn);
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
        inOptions = !inOptions;

        lArrowBtn.addAction(Actions.moveTo(-lArrowBtn.getWidth(), lArrowBtn.getY(), 1f, Interpolation.smoother));
        rArrowBtn.addAction(Actions.moveTo(stage.getWidth(), rArrowBtn.getY(), 1f, Interpolation.smoother));
        curSavInfo.addAction(Actions.moveTo(curSavInfo.getX(), -curSavInfo.getHeight(), 1f, Interpolation.smoother));
        curSavImg.addAction(Actions.moveTo(curSavImg.getX(), -curSavImg.getHeight(), 2f, Interpolation.smoother));
        menuTable.addAction(Actions.moveTo(menuTable.getX(), -menuTable.getY() - menuTable.getHeight() - 48f, 1f, Interpolation.smoother));

        brandLogo.clearActions();
        brandLogo.addAction(Actions.moveTo(brandLogo.getX(), stage.getHeight() + brandLogo.getHeight(), 1f, Interpolation.smoother));

        // Main options window:
        final Table optionsTable = new Table(skin);
        optionsTable.setBackground("bg");
        optionsTable.align(Align.top | Align.center);
        optionsTable.setSize(768f, 640f);
        optionsTable.setX((stage.getWidth() / 2f) - (optionsTable.getWidth() / 2f));
        stage.addActor(optionsTable);

        float paddingBottom = 10f;

        // Options title:
        Label optTitle = new Label(game.locale.TranslatableText("options.title"), skin, "header_with_bg");
        optTitle.setAlignment(Align.center);
        optionsTable.add(optTitle).width(optionsTable.getWidth()).row();

        Table contentTable = new Table();
        contentTable.align(Align.top | Align.center);
        contentTable.setWidth(optionsTable.getWidth());

        // Scroll panel for options:
        ScrollPane optScroll = new ScrollPane(contentTable);
        optScroll.setScrollingDisabled(true, false);
        optionsTable.add(optScroll).width(optionsTable.getWidth()).height(512f).padBottom(paddingBottom).row();

        // - - -  General category  - - -:
        Label genLabel = new Label(game.locale.TranslatableText("options.general"), skin, "subheader_with_bg");
        contentTable.add(genLabel).fillX().padBottom(paddingBottom).row();

        Table genCategory = new Table();
        contentTable.add(genCategory).fillX().padBottom(paddingBottom).row();

        // Show debug:
        Label debLabel = new Label(game.locale.TranslatableText("options.debug"), skin);
        debLabel.setAlignment(Align.left);
        genCategory.add(debLabel).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom);

        final TextButton debButton = new TextButton((game.prefs.getBoolean("debug", false)) ? "ON" : "OFF", skin);

        debButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("debug", false);

                game.prefs.putBoolean("debug", !value);

                value = !value;

                if (value) stage.addActor(debugInfo);
                else debugInfo.remove();

                debButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        genCategory.add(debButton).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom).row();

        // - - -  Audio category  - - -:
        Label audioLabel = new Label(game.locale.TranslatableText("options.audio"), skin, "subheader_with_bg");
        contentTable.add(audioLabel).fillX().padBottom(paddingBottom).row();

        Table audioCategory = new Table();
        contentTable.add(audioCategory).fillX().padBottom(paddingBottom).row();

        // Music:
        Label musLabel = new Label(game.locale.TranslatableText("options.music"), skin);
        musLabel.setAlignment(Align.left);
        audioCategory.add(musLabel).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom);

        final TextButton musButton = new TextButton((game.prefs.getBoolean("music", true)) ? "ON" : "OFF", skin);

        musButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("music", true);

                game.prefs.putBoolean("music", !value);

                value = !value;

                if (value) menuMusic.play();
                else menuMusic.pause();

                musButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        audioCategory.add(musButton).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom).row();

        // Sound:
        Label sndLabel = new Label(game.locale.TranslatableText("options.sound"), skin);
        sndLabel.setAlignment(Align.left);
        audioCategory.add(sndLabel).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom);

        final TextButton sndButton = new TextButton((game.prefs.getBoolean("sfx", true)) ? "ON" : "OFF", skin);

        sndButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("sfx", true);

                game.prefs.putBoolean("sfx", !value);

                value = !value;

                sndButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        audioCategory.add(sndButton).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom).row();

        // - - -  Video category  - - -:
        Label videoLabel = new Label(game.locale.TranslatableText("options.video"), skin, "subheader_with_bg");
        contentTable.add(videoLabel).fillX().padBottom(paddingBottom).row();

        Table videoCategory = new Table();
        contentTable.add(videoCategory).fillX().padBottom(paddingBottom).row();

        // Vertical sync:
        Label vscLabel = new Label(game.locale.TranslatableText("options.vsync"), skin);
        vscLabel.setAlignment(Align.left);
        videoCategory.add(vscLabel).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom);

        final TextButton vscButton = new TextButton((game.prefs.getBoolean("vsync", true)) ? "ON" : "OFF", skin);

        vscButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("vsync", true);

                game.prefs.putBoolean("vsync", !value);

                value = !value;

                Gdx.graphics.setVSync(value);

                vscButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        videoCategory.add(vscButton).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom).row();

        // Full screen:
        Label fscLabel = new Label(game.locale.TranslatableText("options.fullscreen"), skin);
        fscLabel.setAlignment(Align.left);
        videoCategory.add(fscLabel).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom);

        final TextButton fscButton = new TextButton((game.prefs.getBoolean("fullscreen", true)) ? "ON" : "OFF", skin);

        fscButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean value = game.prefs.getBoolean("fullscreen", true);

                game.prefs.putBoolean("fullscreen", !value);

                value = !value;
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

                if (value) Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                else Gdx.graphics.setWindowedMode(dim.width, dim.height);

                fscButton.getLabel().setText((value) ? "ON" : "OFF");
            }
        });

        videoCategory.add(fscButton).width(optionsTable.getWidth() / 2f).padBottom(paddingBottom).row();

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

                String[] fh4Locale = fhNext.nameWithoutExtension().split("_");
                Locale locale = new Locale(fh4Locale[0], fh4Locale[1]);

                langButton.setText(game.locale.FormattedText("options.language", locale.getDisplayLanguage(), locale.getDisplayCountry()));
                game.setScreen(new SplashScreen(game));
                menuMusic.stop();
            }
        });

        contentTable.add(langButton).width(optionsTable.getWidth()).padBottom(paddingBottom).row();

        // - - -  Reset save data  - - -:
        TextButton resetButton = new TextButton(game.locale.TranslatableText("options.reset"), skin);
        contentTable.add(resetButton).width(optionsTable.getWidth()).padBottom(paddingBottom).row();

        // Game info:
        Label infLabel = new Label(String.format("%s - %s", MaxonConstants.GAME_NAME, MaxonConstants.GAME_VERSION), skin, "small-default");
        infLabel.setAlignment(Align.center);
        optionsTable.add(infLabel).maxWidth(optionsTable.getWidth() / 2f).row();

        // // Action buttons:
        Table actTable = new Table(skin);
        actTable.setBackground("fg");
        actTable.setWidth(optionsTable.getWidth());
        actTable.align(Align.right);
        optionsTable.add(actTable).width(optionsTable.getWidth()).maxWidth(optionsTable.getWidth()).pad(5f);

        TextButton closeBtn = new TextButton("Back to main menu", skin);

        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inOptions = !inOptions;
                optionsTable.addAction(Actions.moveTo(optionsTable.getX(), -stage.getHeight(), 2f, Interpolation.smoother));

                brandLogo.clearActions();
                brandLogo.addAction(
                        Actions.sequence(
                                Actions.parallel(
                                        Actions.rotateTo(0f, 1f),
                                        Actions.moveTo(
                                                (stage.getWidth() / 2f) - (brandLogo.getWidth() / 2f),
                                                stage.getHeight() - brandLogo.getHeight() * 1.2f,
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

                lArrowBtn.addAction(Actions.moveTo(lArrowBtn.getWidth(), lArrowBtn.getY(), 1f, Interpolation.smoother));
                rArrowBtn.addAction(Actions.moveTo(stage.getWidth() - rArrowBtn.getWidth() * 2, rArrowBtn.getY(), 1f, Interpolation.smoother));
                curSavInfo.addAction(Actions.moveTo(curSavInfo.getX(), 6f, 1f, Interpolation.smoother));
                curSavImg.addAction(Actions.moveTo(curSavImg.getX(), (stage.getHeight() / 2f) - (curSavImg.getHeight() / 2f), 2f, Interpolation.smoother));
                menuTable.addAction(Actions.moveTo(menuTable.getX(), 0, 1f, Interpolation.smoother));
            }
        });

        actTable.add(closeBtn).pad(5f);

        TextButton saveBtn = new TextButton("Apply", skin);

        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.prefs.flush();
            }
        });

        actTable.add(saveBtn).pad(5f);

        optionsTable.setY(-optionsTable.getHeight());
        optionsTable.addAction(Actions.moveTo(optionsTable.getX(), (stage.getHeight() / 2f) - (optionsTable.getHeight() / 2f), 2f, Interpolation.smoother));
    }

    private void generateSaves() {
        ArrayList<MaxonSavegame> saves = GameDataSystem.getSavegames();
        int i = -1;

        for (MaxonSavegame sav : saves) {
            i++;

            savInfos.add(new SavegameInfo(game, skin, sav, i));
            savImgs.add(new Image(
                        PetUtils.animatedImageById(game.assetManager, sav.petId).getDrawable()
                    )
            );
        }

        savInfos.add(new SavegameInfo(game, skin, null, i + 1));
        savImgs.add(new Image(
                mainAtlas.findRegion("unknown")
        ));
    }

    private void updateCurrentVisualSavegame(boolean indexNegative) {
        if (indexNegative) curSav--;
        else curSav++;

        if (curSav < 0) {
            curSav = savInfos.size() - 1;
        }

        if (savInfos.size() - 1 < curSav || savImgs.size() - 1 < curSav) {
            curSav = 0;
        }

        // Set the image:
        if (curSavImg != null) {
            curSavImg.remove();
            curSavImg.setSize(
                    curSavImg.getWidth() / 2f,
                    curSavImg.getHeight() / 2f
            );
        }
        curSavImg = savImgs.get(curSav);

        curSavImg.setSize(
                curSavImg.getWidth() * 2f,
                curSavImg.getHeight() * 2f
        );

        curSavImg.setPosition(
                (stage.getWidth() / 2f) - (curSavImg.getWidth() / 2f),
                (stage.getHeight() / 2f) - (curSavImg.getHeight() / 2f)
        );

        stage.addActor(curSavImg);

        // Set the info:
        if (curSavInfo != null) curSavInfo.remove();

        curSavInfo = savInfos.get(curSav);

        curSavInfo.setPosition(
                (stage.getWidth() / 2f) - (curSavInfo.getWidth() / 2f),
                6f
        );

        stage.addActor(curSavInfo);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() {
        stage.dispose();
    }
}
