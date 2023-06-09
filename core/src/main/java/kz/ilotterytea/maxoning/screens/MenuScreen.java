package kz.ilotterytea.maxoning.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.maxoning.MaxoningGame;
import kz.ilotterytea.maxoning.localization.LineId;
import kz.ilotterytea.maxoning.savegames.Savegame;

import java.util.ArrayList;

public class MenuScreen implements Screen {
    private final MaxoningGame GAME = MaxoningGame.getInstance();

    private Stage stage;

    private Skin skin;
    private TextureAtlas textureAtlas;

    private Savegame savegame;

    private Image tintedImage;

    @Override
    public void show() {
        savegame = Savegame.loadFromFile("savegame.dat").orElse(null);

        generateUi();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.56f, 0.41f, 0.25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void generateUi() {
        // Initializing stage, skin and texture atlas
        stage = new Stage(new ScreenViewport());
        stage.setDebugAll(true);

        textureAtlas = GAME.getAssetManager().get("main_spritesheet.atlas", TextureAtlas.class);
        skin = GAME.getAssetManager().get("main_spritesheet.skin", Skin.class);

        // Setting input processor
        Gdx.input.setInputProcessor(stage);

        // Tint the background
        tintedImage = new Image(textureAtlas.findRegion("sample16"));
        tintedImage.setFillParent(true);

        tintedImage.setColor(new Color(0.1f, 0.1f, 0.1f, 1f));

        // Fade out:
        tintedImage.addAction(
                Actions.sequence(
                        Actions.delay(1f),
                        Actions.fadeOut(1f)
                )
        );

        // Adding tint:
        stage.addActor(tintedImage);

        // --------------- MAIN TABLE ---------------
        Table mainTable = new Table();
        mainTable.align(Align.bottomLeft);
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // --------------- LOGO ---------------
        Image logoImage = new Image(new Texture(Gdx.files.internal("sprites/logo.png")));
        logoImage.setOrigin(
                logoImage.getWidth() / 2f,
                logoImage.getHeight() / 2f
        );

        // Animating the logo image
        logoImage.addAction(
                Actions.sequence(
                        Actions.delay(1f),
                        Actions.repeat(
                                RepeatAction.FOREVER,
                                Actions.sequence(
                                        Actions.parallel(
                                                Actions.scaleTo(0.9f, 0.9f, 10f, Interpolation.fade),
                                                Actions.rotateTo(5f, 10f, Interpolation.fade)
                                        ),
                                        Actions.parallel(
                                                Actions.scaleTo(1f, 1f, 10f, Interpolation.fade),
                                                Actions.rotateTo(-5f, 10f, Interpolation.fade)
                                        )
                                )
                        )
                )
        );

        // Adding the logo to the stage
        mainTable.add(logoImage).top().pad(32f).expand().row();

        // --------------- "MAXON" ICON ---------------
        Image maxonImage = new Image(textureAtlas.findRegion("maxon"));
        maxonImage.addAction(
                Actions.repeat(
                        RepeatAction.FOREVER,
                        Actions.sequence(
                                Actions.moveBy(0, 10f, 0.1f, Interpolation.linear),
                                Actions.moveBy(0, -10f, 0.1f, Interpolation.linear),
                                Actions.parallel(
                                        Actions.sizeBy(10f, -10f, 0.1f, Interpolation.linear),
                                        Actions.moveBy(-5f, 0f, 0.1f, Interpolation.linear)
                                ),
                                Actions.parallel(
                                        Actions.sizeBy(-10f, 10f, 0.1f, Interpolation.linear),
                                        Actions.moveBy(5f, 0f, 0.1f, Interpolation.linear)
                                ),
                                Actions.delay(0.5f)
                        )
                )
        );

        mainTable.add(maxonImage).center().expand().row();

        // --------------- "PRESS ANY KEY" LABEL ---------------
        Label pressAnyKeyLabel = new Label(GAME.getLocalizationManager().literalText(GAME.getSettingsPreferences().getString("language"), LineId.MENU_PRESS_START), skin, "bold");
        pressAnyKeyLabel.addAction(
                Actions.repeat(
                        RepeatAction.FOREVER,
                        Actions.sequence(
                                Actions.fadeOut(0.5f),
                                Actions.fadeIn(0.5f),
                                Actions.delay(0.5f)
                        )
                )
        );
        mainTable.add(pressAnyKeyLabel).expand().row();

        if (savegame != null) {
            // TODO
        }

        // --------------- TABLE FOR MENU BUTTONS ---------------
        Table menuTable = new Table();
        mainTable.add(menuTable).fill();

        // --------------- LEFT MENU CONTROLLERS ---------------
        // Maximum sizes for widgets
        final float WIDGET_HEIGHT = 32f;

        // Creating a table for left menu controllers (buttons)
        Table leftMenuTable = new Table();
        leftMenuTable.align(Align.left);
        menuTable.add(leftMenuTable).growX();

        // --------------- EXIT BUTTON ---------------
        // Creating an exit button
        ImageButton exitButton = new ImageButton(skin, "exit_button");
        leftMenuTable.add(exitButton).pad(8f).size(WIDGET_HEIGHT * (exitButton.getWidth() / exitButton.getHeight()), WIDGET_HEIGHT);

        // Adding behaviour for the exit button:
        final boolean[] isExiting = {false};

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isExiting[0]) {
                    isExiting[0] = true;

                    tintedImage.addAction(Actions.fadeIn(1f));
                    tintedImage.toFront();

                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            Gdx.app.exit();
                        }
                    }, 1f);
                }
            }
        });

        // --------------- RIGHT MENU CONTROLLERS ---------------
        // Creating a table for left menu controllers (buttons)
        Table rightMenuTable = new Table();
        rightMenuTable.align(Align.right);

        menuTable.add(rightMenuTable).growX();

        // --------------- MUSIC BUTTON ---------------
        // Creating an music button
        ImageButton musicButton = new ImageButton(skin, (GAME.getSettingsPreferences().getBoolean("music", true)) ? "music_on_button" : "music_off_button");
        rightMenuTable.add(musicButton).pad(8f).size(WIDGET_HEIGHT * (musicButton.getWidth() / musicButton.getHeight()), WIDGET_HEIGHT);

        // Adding behaviour for the music button:
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean isEnabled = !GAME.getSettingsPreferences().getBoolean("music", true);

                Drawable up = skin.getDrawable((isEnabled) ? "music_on" : "music_off");
                ImageButton.ImageButtonStyle style = musicButton.getStyle();
                style.up = up;

                musicButton.setStyle(style);
                musicButton.setWidth(WIDGET_HEIGHT * (up.getMinWidth() / up.getMinHeight()));

                GAME.getSettingsPreferences().putBoolean("music", isEnabled);
                GAME.getSettingsPreferences().flush();
            }
        });

        // --------------- FULLSCREEN BUTTON ---------------
        // Creating a fullscreen button
        ImageButton fullScreenButton = new ImageButton(skin, (GAME.getSettingsPreferences().getBoolean("fullscreen", false)) ? "windowed_screen_button" : "full_screen_button");
        rightMenuTable.add(fullScreenButton).pad(8f).size(WIDGET_HEIGHT * (fullScreenButton.getWidth() / fullScreenButton.getHeight()), WIDGET_HEIGHT);

        // Adding behaviour for the fullscreen button:
        fullScreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean isEnabled = !GAME.getSettingsPreferences().getBoolean("fullscreen", false);

                Drawable up = skin.getDrawable((isEnabled) ? "windowed" : "fullscreen");
                ImageButton.ImageButtonStyle style = fullScreenButton.getStyle();
                style.up = up;

                fullScreenButton.setStyle(style);
                fullScreenButton.setWidth(WIDGET_HEIGHT * (up.getMinWidth() / up.getMinHeight()));

                GAME.getSettingsPreferences().putBoolean("fullscreen", isEnabled);
                GAME.getSettingsPreferences().flush();

                if (isEnabled) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
                }
            }
        });

        // --------------- LOCALIZATION BUTTON ---------------
        // Setting the image for the localization flag image
        Drawable localizationFlagDrawable = skin.getDrawable(
                GAME.getSettingsPreferences().getString(
                        "language",
                        new ArrayList<>(GAME.getLocalizationManager().getLoadedLocalizations().keySet())
                                .get(0)
                )
        );

        // Creating a localization flag image
        Image localizationFlagImage = new Image(localizationFlagDrawable);
        rightMenuTable.add(localizationFlagImage).pad(8f).size(WIDGET_HEIGHT * (localizationFlagImage.getWidth() / localizationFlagImage.getHeight()), WIDGET_HEIGHT);

        // Adding behaviour for the localization flag image
        localizationFlagImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArrayList<String> localeIds = new ArrayList<>(GAME.getLocalizationManager().getLoadedLocalizations().keySet());

                String localeId = GAME.getSettingsPreferences().getString(
                        "language",
                        new ArrayList<>(GAME.getLocalizationManager().getLoadedLocalizations().keySet())
                                .get(0)
                );

                int localeIdIndex = localeIds.indexOf(localeId) + 1;

                if (localeIdIndex >= localeIds.size()) {
                    localeIdIndex = 0;
                }

                localeId = localeIds.get(localeIdIndex);

                Drawable localeIdDrawable = skin.getDrawable(localeId);
                localizationFlagImage.setDrawable(localeIdDrawable);
                localizationFlagImage.setWidth(WIDGET_HEIGHT * (localizationFlagImage.getWidth() / localizationFlagImage.getHeight()));

                GAME.getSettingsPreferences().putString("language", localeId);
                GAME.getSettingsPreferences().flush();

                GAME.setScreen(new MenuScreen());
            }
        });
    }
}
