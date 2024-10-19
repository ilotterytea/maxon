package kz.ilotterytea.maxon.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.MaxonGame;
import kz.ilotterytea.maxon.player.Savegame;
import kz.ilotterytea.maxon.ui.*;
import kz.ilotterytea.maxon.utils.GameUpdater;
import kz.ilotterytea.maxon.utils.I18N;
import kz.ilotterytea.maxon.utils.OsUtils;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.util.ArrayList;

public class MenuScreen implements Screen {
    private final MaxonGame game;

    private final Stage stage;
    private final Music menuMusic;

    private final Savegame savegame = Savegame.getInstance();

    private SceneManager sceneManager;
    private PerspectiveCamera camera;

    private final ArrayList<Timer.Task> tasks = new ArrayList<>();
    private Sound clickSound;

    public MenuScreen() {
        this.game = MaxonGame.getInstance();

        // Stage and skin:
        this.stage = new Stage(new ScreenViewport());
        this.stage.addAction(Actions.sequence(Actions.alpha(0.0f), Actions.alpha(1.0f, 1f)));

        Skin uiSkin = game.assetManager.get("sprites/gui/ui.skin", Skin.class);
        Skin skin = game.assetManager.get("MainSpritesheet.skin", Skin.class);
        Skin widgetSkin = game.assetManager.get("sprites/gui/widgets.skin", Skin.class);
        TextureAtlas brandAtlas = game.assetManager.get("sprites/gui/brand.atlas", TextureAtlas.class);
        TextureAtlas widgetAtlas = game.assetManager.get("sprites/gui/widgets.atlas", TextureAtlas.class);

        Skin friendsSkin = game.assetManager.get("sprites/gui/friends.skin", Skin.class);

        // Main Menu music:
        this.menuMusic = game.assetManager.get("mus/menu/mus_menu_loop.mp3", Music.class);
        menuMusic.setLooping(true);

        clickSound = game.assetManager.get("sfx/ui/click.ogg", Sound.class);

        // Tint the background
        Image tintImage = new Image(skin, "tint");
        tintImage.setFillParent(true);
        this.stage.addActor(tintImage);

        // - - - - - -  U I  - - - - - -
        Table menuTable = new Table();
        menuTable.setFillParent(true);

        // - - -  Brand - - -
        Table brandTable = new Table();

        Image logo = new Image(brandAtlas.findRegion("brand"));

        logo.setOrigin(
                logo.getWidth() / 2f,
                logo.getHeight() / 2f
        );

        if (OsUtils.isMobile) {
            logo.addAction(
                    Actions.repeat(
                            RepeatAction.FOREVER,
                            Actions.sequence(
                                    Actions.scaleTo(0.9f, 0.9f, 5f, Interpolation.smoother),
                                    Actions.scaleTo(1.0f, 1.0f, 5f, Interpolation.smoother)
                            )
                    )
            );

            float stageWidth = this.stage.getWidth() - 10f;
            float difference = stageWidth / logo.getWidth();

            brandTable.add(logo).size(stageWidth, logo.getHeight() * difference);
        } else {
            logo.addAction(
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

            brandTable.add(logo);
        }

        // - - -  Menu control (quit, options, etc.) - - -
        Table controlTable = new Table(skin);
        controlTable.align(Align.top | Align.center);
        controlTable.pad(6f);

        // Left part of menu control
        Table leftGameControlTable = new Table();
        leftGameControlTable.align(Align.left);

        ShakingImageButton quitButton = new ShakingImageButton(widgetSkin, "exit");

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                clickSound.play();
                Gdx.app.exit();
            }
        });

        leftGameControlTable.add(quitButton).padRight(12f);

        // Right part of menu control
        Table rightGameControlTable = new Table();
        if (OsUtils.isMobile) {
            rightGameControlTable.align(Align.center);
        } else {
            rightGameControlTable.align(Align.right);
        }

        // - - -  D E V E L O P E R  S H O W C A S E  - - -
        final int[] developerIndex = {0};
        ShakingImageButton developerImage = new ShakingImageButton(friendsSkin, MaxonConstants.GAME_DEVELOPERS[developerIndex[0]][0]);
        developerImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.net.openURI(MaxonConstants.GAME_DEVELOPERS[developerIndex[0]][1]);
                clickSound.play();
            }
        });
        developerImage.setSize(64, 64);

        tasks.add(Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                developerIndex[0]++;

                if (developerIndex[0] >= MaxonConstants.GAME_DEVELOPERS.length) {
                    developerIndex[0] = 0;
                }

                String[] dev = MaxonConstants.GAME_DEVELOPERS[developerIndex[0]];

                developerImage.clearActions();
                developerImage.addAction(
                        Actions.sequence(
                                Actions.alpha(0.0f, 1f),
                                new Action() {
                                    @Override
                                    public boolean act(float delta) {
                                        developerImage.setDrawable(friendsSkin, dev[0]);
                                        return true;
                                    }
                                },
                                Actions.alpha(1.0f, 1f)
                        )
                );

                developerImage.clearListeners();
                developerImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        Gdx.net.openURI(dev[1]);
                        clickSound.play();
                    }
                });
            }
        }, 5, 5));

        // Localization
        String[] fh4Locale = game.locale.getFileHandle().nameWithoutExtension().split("_");
        String localeButtonStyleName = "locale_" + fh4Locale[0];
        ShakingImageButton localeButton = new ShakingImageButton(widgetSkin, localeButtonStyleName);

        localeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

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

                game.setScreen(new SplashScreen());
                menuMusic.stop();
                clickSound.play();
            }
        });

        // Music button
        String musicButtonStyleName;

        if (game.prefs.getBoolean("music")) {
            musicButtonStyleName = "music_on";
            menuMusic.play();
        } else {
            musicButtonStyleName = "music_off";
        }

        ShakingImageButton musicButton = new ShakingImageButton(widgetSkin, musicButtonStyleName);
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                String style;

                if (game.prefs.getBoolean("music")) {
                    style = "music_off";
                    menuMusic.pause();
                } else {
                    style = "music_on";
                    menuMusic.play();
                }

                game.prefs.putBoolean("music", !game.prefs.getBoolean("music"));
                game.prefs.flush();

                musicButton.setDrawable(widgetSkin, style);
                clickSound.play();
            }
        });

        if (!OsUtils.isMobile) {
            rightGameControlTable.add(developerImage).padRight(16f);
            rightGameControlTable.add(localeButton).padRight(16f);
            rightGameControlTable.add(musicButton).padRight(16f);

            // Resolution button
            String resolutionButtonStyleName;

            if (game.prefs.getBoolean("fullscreen")) {
                resolutionButtonStyleName = "windowed";
            } else {
                resolutionButtonStyleName = "fullscreen";
            }

            ShakingImageButton resolutionButton = new ShakingImageButton(widgetSkin, resolutionButtonStyleName);
            resolutionButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    String style;

                    if (game.prefs.getBoolean("fullscreen")) {
                        style = "fullscreen";
                        Gdx.graphics.setWindowedMode(game.prefs.getInteger("width", 800), game.prefs.getInteger("height", 600));
                    } else {
                        style = "windowed";
                        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    }

                    game.prefs.putBoolean("fullscreen", !game.prefs.getBoolean("fullscreen"));
                    game.prefs.flush();

                    resolutionButton.setDrawable(widgetSkin, style);
                    clickSound.play();
                }
            });
            rightGameControlTable.add(resolutionButton);

            controlTable.add(leftGameControlTable).grow();
        } else {
            rightGameControlTable.add(developerImage).expand();
            rightGameControlTable.add(localeButton).expand();
            rightGameControlTable.add(musicButton).expand();
        }

        controlTable.add(rightGameControlTable).grow();

        // - - -  Savegame  - - -
        Table savegameTable = new Table();
        SavegameWidget info = new SavegameWidget(this.game, uiSkin, stage, savegame);

        if (OsUtils.isMobile) {
            savegameTable.add(info).growX().minHeight(240f).pad(16f);
        } else {
            savegameTable.add(info).minSize(640f, 240f);
        }

        // Suggest an update
        if (!GameUpdater.CLIENT_IS_ON_LATEST_VERSION && OsUtils.isPC) {
            TextButton updateButton = new TextButton(game.locale.TranslatableText("updater.info"), uiSkin, "link");

            updateButton.setPosition(8f, stage.getHeight() - 32f);

            updateButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Gdx.net.openURI(MaxonConstants.GAME_APP_URL);
                }
            });

            menuTable.add(updateButton).pad(6f).left().row();
        }

        // Adding tables into the main UI table
        menuTable.add(brandTable).grow().row();
        menuTable.add(savegameTable).grow().row();
        menuTable.add(controlTable).growX();

        this.stage.addActor(menuTable);

        DebugWidget debugWidget = new DebugWidget(uiSkin);
        this.stage.addActor(debugWidget);

        create3D();
        Gdx.input.setInputProcessor(stage);
    }

    @Override public void show() {
        if (game.prefs.getBoolean("music", true)) menuMusic.play();

        // Start to render:
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        sceneManager.update(delta);
        sceneManager.render();

        camera.rotate(2 * delta, 0, 1, 0);
        camera.update();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        sceneManager.updateViewport(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        for (Timer.Task task : tasks) {
            task.cancel();
        }
        tasks.clear();

        menuMusic.stop();
        dispose();
    }
    @Override public void dispose() {
        stage.dispose();
    }

    private void create3D() {
        sceneManager = new SceneManager();

        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 300f;
        camera.position.set(0f, 5f, 0f);

        camera.update();

        sceneManager.setCamera(camera);

        DirectionalShadowLight light = new DirectionalShadowLight(1024, 1024, 60f, 60f, 1f, 300f);
        light.set(new Color(0xdcccffff), -1f, -0.8f, -0.2f);
        light.intensity = 5f;
        sceneManager.environment.add(light);
        sceneManager.environment.shadowMap = light;

        PointLightEx signLight = new PointLightEx();

        if (!savegame.isNewlyCreated()) {
            signLight.set(Color.PINK, new Vector3(2f, 6f, 2f), 80f, 100f);
        }

        PointLightEx windowLight = new PointLightEx();
        windowLight.set(Color.BLUE, new Vector3(-1.1f, 7.3f, 0.5f), 80f, 100f);

        sceneManager.environment.add(windowLight, signLight);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        Cubemap diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        Cubemap specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        Cubemap environmentCubemap = EnvironmentUtil.createCubemap(
                new InternalFileHandleResolver(),
                "skyboxes/menu/",
                ".png",
                EnvironmentUtil.FACE_NAMES_NEG_POS
        );

        sceneManager.setSkyBox(new SceneSkybox(environmentCubemap));
    }
}
