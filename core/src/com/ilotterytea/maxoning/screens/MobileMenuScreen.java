package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
import com.ilotterytea.maxoning.anim.SpriteUtils;
import com.ilotterytea.maxoning.player.MaxonSavegame;
import com.ilotterytea.maxoning.ui.AnimatedImage;
import com.ilotterytea.maxoning.ui.MovingChessBackground;
import com.ilotterytea.maxoning.utils.I18N;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Menu screen for mobile devices.
 * @since 1.3
 * @author ilotterytea
 */
public class MobileMenuScreen implements Screen {
    private final MaxonGame game;
    private Stage stage;

    private Skin skin;

    private MovingChessBackground bg;

    private MaxonSavegame sav;

    public MobileMenuScreen(MaxonGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth() / game.prefs.getFloat("scale", 2f), Gdx.graphics.getHeight() / game.prefs.getFloat("scale", 2f)));
        skin = game.assetManager.get("MainSpritesheet.skin", Skin.class);

        TextureAtlas brandAtlas = game.assetManager.get("sprites/gui/brand.atlas", TextureAtlas.class);

        sav = GameDataSystem.load("latest.sav");
        Music menuMusic = game.assetManager.get("mus/menu/mus_menu_loop.ogg", Music.class);
        menuMusic.setLooping(true);
        menuMusic.play();

        // Background:
        bg = new MovingChessBackground(
                1,
                1,
                stage.getWidth(),
                stage.getHeight(),
                skin.getDrawable("tile_01"),
                skin.getDrawable("tile_02")
        );

        // Cat:
        AnimatedImage cat = new AnimatedImage(
                SpriteUtils.splitToTextureRegions(game.assetManager.get("sprites/sheet/loadingCircle.png", Texture.class),
                        112, 112, 10, 5
                )
        );
        cat.setOrigin(
                cat.getWidth() / 2f,
                cat.getHeight() / 2f
        );
        cat.setPosition(
                (stage.getWidth() / 2f) - (cat.getWidth() / 2f),
                (stage.getHeight() / 2f) - (cat.getHeight() / 2f) + 128f
        );
        cat.addAction(
                Actions.repeat(
                        RepeatAction.FOREVER,
                        Actions.sequence(
                                Actions.rotateTo(-5, 5f, Interpolation.smoother),
                                Actions.rotateTo(5, 5f, Interpolation.smoother)
                        )
                )
        );

        stage.addActor(cat);

        // - - - - - -  L O G O  - - - - - - :
        Image logoImage = new Image(brandAtlas.findRegion("brand"));
        logoImage.setScale(0.5f);
        logoImage.setPosition(
                (stage.getWidth() / 2f) - (logoImage.getWidth() / 2f),
                (stage.getHeight() / 2f) - (logoImage.getHeight() / 2f) + 128f
        );
        logoImage.setOrigin(
                logoImage.getWidth() / 2f,
                logoImage.getHeight() / 2f
        );
        logoImage.addAction(
                Actions.repeat(
                        RepeatAction.FOREVER,
                        Actions.sequence(
                                Actions.parallel(
                                        Actions.rotateTo(-5, 5f, Interpolation.smoother),
                                        Actions.scaleTo(0.45f, 0.45f, 5f, Interpolation.smoother)
                                ),
                                Actions.parallel(
                                        Actions.rotateTo(5, 5f, Interpolation.smoother),
                                        Actions.scaleTo(0.5f, 0.5f, 5f, Interpolation.smoother)
                                )
                        )
                )
        );


        stage.addActor(logoImage);

        // - - - - - -  M E N U  - - - - - - :
        Table menuTable = new Table();
        menuTable.setSize(stage.getWidth(), stage.getHeight());
        menuTable.setPosition(0, 0);
        menuTable.align(Align.bottom | Align.center);
        menuTable.pad(20f);

        stage.addActor(menuTable);

        // Play button:
        final TextButton startBtn = new TextButton(
                (sav == null) ?
                        game.locale.TranslatableText("menu.playGame") :
                        game.locale.TranslatableText("menu.continue"),
                skin
        );

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

        menuTable.add(startBtn).width(256f).pad(6f).row();

        // Table for lang and reset buttons:
        Table langResetTable = new Table();
        menuTable.add(langResetTable).width(256f).row();

        // Language button:
        String[] fh4Locale = game.locale.getFileHandle().nameWithoutExtension().split("_");
        Locale locale = new Locale(fh4Locale[0], fh4Locale[1]);
        final TextButton langBtn = new TextButton(locale.getDisplayLanguage(), skin);

        langBtn.addListener(new ClickListener() {
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

                langBtn.setText(locale.getDisplayLanguage());
                game.setScreen(new SplashScreen(game));
            }
        });

        langResetTable.add(langBtn).width(256f);//.padRight(6f);

        // Reset save button:
        //TextButton resetBtn = new TextButton("Reset", widgetSkin);
        //langResetTable.add(resetBtn).width(125f);

        Gdx.input.setInputProcessor(stage);

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
        stage.getViewport().update(width, height, true);

        bg.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() { dispose(); }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
