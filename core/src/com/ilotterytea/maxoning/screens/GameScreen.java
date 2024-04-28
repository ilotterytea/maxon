package com.ilotterytea.maxoning.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ilotterytea.maxoning.MaxonConstants;
import com.ilotterytea.maxoning.MaxonGame;
import com.ilotterytea.maxoning.anim.SpriteUtils;
import com.ilotterytea.maxoning.audio.Playlist;
import com.ilotterytea.maxoning.inputprocessors.CrossProcessor;
import com.ilotterytea.maxoning.player.DecalPlayer;
import com.ilotterytea.maxoning.player.MaxonItem;
import com.ilotterytea.maxoning.player.MaxonItemRegister;
import com.ilotterytea.maxoning.player.MaxonSavegame;
import com.ilotterytea.maxoning.screens.game.shop.ShopUI;
import com.ilotterytea.maxoning.ui.*;
import com.ilotterytea.maxoning.utils.math.Math;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen, InputProcessor {
    final MaxonGame game;
    final int slotId;
    final long playTimestamp;
    boolean isShopping = false, isInventoryEnabled = false;

    MaxonSavegame player;

    Stage stage;
    Skin skin;

    TextureAtlas mainAtlas;

    Label pointsLabel, multiplierLabel;
    AnimatedImage cat;
    AnimatedImageButton maxon;

    Table boardTable, quickTable;

    Dialog notEnoughPointsDialog;

    ArrayList<MaxonItem> items;
    Map<Integer, Integer> invItems;

    MovingChessBackground bg;
    Playlist playlist;

    private SceneManager sceneManager;
    private PerspectiveCamera camera;

    private DecalBatch decalBatch;
    private ArrayList<Decal> decals;
    private DecalPlayer decalPlayer;

    public GameScreen(MaxonGame game, MaxonSavegame sav, int slotId) throws IOException, ClassNotFoundException {
        this.game = game;
        this.slotId = slotId;
        this.playTimestamp = System.currentTimeMillis();

        create3D();

        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
        decals = new ArrayList<>();

        TextureRegion[] playerTextureRegions = SpriteUtils.splitToTextureRegions(game.assetManager.get("sprites/sheet/loadingCircle.png", Texture.class), 112, 112, 10, 5);
        decalPlayer = new DecalPlayer(playerTextureRegions);
        decals.add(decalPlayer.getDecal());

        playlist = new Playlist(
                game.assetManager.get("mus/game/onwards.wav", Music.class),
                game.assetManager.get("mus/game/paris.wav", Music.class),
                game.assetManager.get("mus/game/adieu.wav", Music.class),
                game.assetManager.get("mus/game/shopping_spree.wav", Music.class)
        );
        playlist.setShuffleMode(true);
        if (game.prefs.getBoolean("music", true)) playlist.next();

        player = sav;
        items = new ArrayList<>();

        createStageUI();

        Gdx.input.setInputProcessor(new InputMultiplexer(this, new CrossProcessor(), stage));
    }

    @Override
    public void show() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                GameDataSystem.save(player, "latest.sav");
            }
        }, 10, 10);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                float multiplier = 0;

                for (MaxonItem item : items) {
                    multiplier += item.multiplier;
                }

                player.points += multiplier;

                final TypingLabel label = new TypingLabel(game.locale.FormattedText("game.newPoint", MaxonConstants.DECIMAL_FORMAT.format(player.multiplier)), skin, "default");

                label.setPosition(
                        maxon.getX(),
                        maxon.getY() + maxon.getHeight()
                );

                label.setWidth(maxon.getWidth());

                label.setAlignment(Align.center);

                label.addAction(Actions.parallel(
                        Actions.fadeOut(5f),
                        Actions.moveTo(
                                label.getX(), label.getY() + Math.getRandomNumber(10, 156), 5f, Interpolation.exp5Out)
                ));

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        label.remove();
                    }
                }, 10f);

                stage.addActor(label);
            }
        }, 5, 5);

        // Random gifts:
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                final ImageButton gift = new ImageButton(skin, "gift");
                gift.setPosition(stage.getWidth() + gift.getWidth(), Math.getRandomNumber((int) gift.getHeight(), (int) stage.getHeight() - (int) gift.getHeight()));
                gift.addAction(
                        Actions.repeat(
                                3,
                                Actions.sequence(
                                        Actions.moveTo(-gift.getWidth(), gift.getY(), 15f, Interpolation.linear),
                                        Actions.moveTo(stage.getWidth() + gift.getWidth(), Math.getRandomNumber((int) gift.getHeight(), (int) stage.getHeight() - (int) gift.getHeight()), 15f, Interpolation.linear)
                                )
                        )
                );


                gift.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        int giftId = Math.getRandomNumber(0, 25);
                        final TypingLabel label = new TypingLabel(game.locale.TranslatableText("gifts.empty"), skin);

                        switch (giftId) {
                            // Points
                            case 0:
                                int randPoints = Math.getRandomNumber(150, 3000);
                                label.setText(game.locale.FormattedText("gifts.points", String.valueOf(randPoints)));
                                player.points += randPoints;
                                break;

                            // Multiplier
                            case 1:
                                int randMp = Math.getRandomNumber(1, 10);
                                label.setText(game.locale.FormattedText("gifts.multiplier", String.valueOf(randMp)));
                                player.multiplier += randMp;
                                break;


                            // Random pet
                            case 2:
                                int randPet = Math.getRandomNumber(0, 1);
                                assert MaxonItemRegister.get(randPet) != null;
                                String name = MaxonItemRegister.get(randPet).name;
                                label.setText(game.locale.FormattedText("gifts.new_pet", name));
                                player.inv.add(randPet);
                                if (invItems.containsKey(randPet)) {
                                    invItems.put(randPet, invItems.get(randPet) + 1);
                                } else {
                                    invItems.put(randPet, 1);
                                }
                                break;
                            // Default
                            default:
                                break;
                        }

                        label.setPosition(
                                gift.getX(),
                                gift.getY()
                        );

                        label.addAction(Actions.sequence(
                                Actions.delay(3f),
                                Actions.fadeOut(2f)
                        ));

                        stage.addActor(label);
                        gift.remove();

                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                label.remove();
                            }
                        }, 5f);
                    }
                });

                stage.addActor(gift);
            }
        }, 600, 600);

        camera.update();
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (game.prefs.getBoolean("music", true) && !playlist.getPlayingNow().isPlaying()) {
            playlist.next();
        }

        // i've temporarily commented it all out while i set up 3d
        //game.batch.begin();

        //bg.draw(game.batch);

        //game.batch.end();

        // Update the points label:
        //pointsLabel.setText(game.locale.FormattedText("game.points",
        //        MaxonConstants.DECIMAL_FORMAT.format(player.points)
        //));

        // Update the multiplier label:
        //multiplierLabel.setText(game.locale.FormattedText("game.multiplier",
        //        MaxonConstants.DECIMAL_FORMAT.format(player.multiplier)
        //));

        // Render 3D
        sceneManager.update(Gdx.graphics.getDeltaTime());
        sceneManager.render();

        this.decalPlayer.render(this.camera);

        for (Decal decal : this.decals) {
            decal.lookAt(this.camera.position, this.camera.up);
            this.decalBatch.add(decal);
        }

        this.decalBatch.flush();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
        sceneManager.updateViewport(width, height);
    }

    private void showShop() {
        // - - - - - -  S H O P  T A B L E  - - - - - - :
        final Table shopTable = new Table(skin);
        shopTable.setBackground("bg");
        shopTable.setSize(stage.getWidth() - 20f, stage.getHeight() - (boardTable.getHeight() + quickTable.getHeight() + 20f));
        shopTable.setPosition(10f, quickTable.getHeight() + 10f);
        shopTable.align(Align.top | Align.center);

        stage.addActor(shopTable);

        // Header table:
        Table headShopTable = new Table();
        shopTable.add(headShopTable).width(shopTable.getWidth()).row();

        // - - -  S H O P  T I T L E  - - -:
        Label shopTitle = new Label(game.locale.TranslatableText("game.petShop"), skin);
        headShopTable.add(shopTitle).expandX();

        // - - -  C L O S E  B U T T O N  - - - :
        TextButton closeButton = new TextButton("X", skin);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                shopTable.remove();
                isShopping = !isShopping;
            }
        });

        headShopTable.add(closeButton).row();

        // - - -  S H O P  C O N T E N T  - - - :
        Table contentTable = new Table();

        // Adding items to shop:
        for (final MaxonItem item : MaxonItemRegister.getItems()) {
            PurchaseItem p_item = new PurchaseItem(
                    skin,
                    item
            );

            p_item.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (player.points < item.price) {
                        notEnoughPointsDialog.show(stage);

                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                notEnoughPointsDialog.hide(Actions.fadeOut(2f, Interpolation.smoother));
                            }
                        }, 2);

                        return;
                    }

                    player.points -= item.price;
                    player.multiplier += item.multiplier;
                    player.inv.add(item.id);

                    if (invItems.containsKey(item.id)) {
                        invItems.put(item.id, invItems.get(item.id) + 1);
                    } else {
                        invItems.put(item.id, 1);
                    }
                }
            });

            contentTable.add(p_item).pad(6f).width(shopTable.getWidth()).row();
        }

        // Scroll panel for content table:
        ScrollPane contentPane = new ScrollPane(contentTable);
        contentPane.setScrollingDisabled(true, false);
        shopTable.add(contentPane);
    }

    private void showInventory() {
        // - - - - - -  I N V E N T O R Y  T A B L E  - - - - - - :
        final Table inventoryTable = new Table(skin);
        inventoryTable.setBackground("bg");
        inventoryTable.setSize(stage.getWidth() - 20f, stage.getHeight() - (boardTable.getHeight() + quickTable.getHeight() + 20f));
        inventoryTable.setPosition(10f, quickTable.getHeight() + 10f);
        inventoryTable.align(Align.top | Align.center);

        stage.addActor(inventoryTable);

        // Header table:
        Table headInventoryTable = new Table();
        inventoryTable.add(headInventoryTable).width(inventoryTable.getWidth()).row();

        // - - -  S H O P  T I T L E  - - -:
        Label inventoryTitle = new Label(game.locale.TranslatableText("game.inventory.title"), skin);
        headInventoryTable.add(inventoryTitle).expandX();

        // - - -  C L O S E  B U T T O N  - - - :
        TextButton closeButton = new TextButton("X", skin);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventoryTable.remove();
                isInventoryEnabled = !isInventoryEnabled;
            }
        });

        headInventoryTable.add(closeButton).row();

        // - - -  I N V E N T O R Y  C O N T E N T  - - - :
        Table contentTable = new Table();
        contentTable.align(Align.left);

        // Adding items to inventory:
        for (int i = 0; i < invItems.keySet().size(); i++) {
            MaxonItem item = MaxonItemRegister.get(i);

            if (item != null) {
                InventoryAnimatedItem invItem = new InventoryAnimatedItem(item, skin, invItems.get(i));
                Cell<InventoryAnimatedItem> cell = contentTable.add(invItem).size(64, 64).pad(5f);

                if (i != 0 && i % (inventoryTable.getWidth() / 69f) == 0) {
                    cell.row();
                }
            }
        };

        // Scroll panel for content table:
        ScrollPane contentPane = new ScrollPane(contentTable);
        contentPane.setScrollingDisabled(true, false);
        inventoryTable.add(contentPane);
    }

    @Override public void pause() {}

    @Override public void resume() {}

    @Override public void hide() {
        playlist.getPlayingNow().stop();
        dispose();
    }

    @Override
    public void dispose() {
        stage.clear();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            player.lastTimestamp = System.currentTimeMillis();
            player.elapsedTime = (System.currentTimeMillis() - playTimestamp) + player.elapsedTime;
            GameDataSystem.save(player, String.format("0%s.maxon", (slotId >= 0) ? slotId : "latest"));

            game.setScreen(new MenuScreen(game));
            dispose();
        }
        //if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
        //    displayPointIncrease();
        //}
        return false;
    }

    private void displayPointIncrease() {
        cat.nextFrame();
        maxon.setDrawable(cat.getDrawable());

        player.points += player.multiplier;

        final TypingLabel label = new TypingLabel(game.locale.FormattedText("game.newPoint", MaxonConstants.DECIMAL_FORMAT.format(player.multiplier)), skin, "default");

        label.setPosition(
                maxon.getX(),
                maxon.getY() + maxon.getHeight()
        );

        label.setWidth(maxon.getWidth());

        label.setAlignment(Align.center);

        label.addAction(Actions.parallel(
                Actions.fadeOut(5f),
                Actions.moveTo(
                        label.getX(), label.getY() + Math.getRandomNumber(10, 156), 5f, Interpolation.exp5Out)
        ));

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                label.remove();
            }
        }, 10f);

        stage.addActor(label);
    }

    private void create3D() {
        SceneAsset sceneAsset = game.assetManager.get("models/scenes/living_room.glb", SceneAsset.class);
        Scene scene = new Scene(sceneAsset.scene);

        sceneManager = new SceneManager();
        sceneManager.addScene(scene);

        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 300f;
        camera.position.set(0.3f, 2.7f, 0.3f);
        camera.rotate(45f, 0f, 1f, 0f);

        camera.update();

        sceneManager.setCamera(camera);

        DirectionalLightEx light = new DirectionalLightEx();
        light.direction.set(0, 2, 0).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        Cubemap environmentCubemap = iblBuilder.buildEnvMap(1024);
        Cubemap diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        Cubemap specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        SceneSkybox skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);
    }

    private void createStageUI() {
        this.stage = new Stage(new ScreenViewport());
        this.skin = this.game.assetManager.get("MainSpritesheet.skin", Skin.class);
        this.mainAtlas = this.game.assetManager.get("MainSpritesheet.atlas", TextureAtlas.class);

        ShopUI shopUI = new ShopUI(this.stage, this.skin, this.mainAtlas);

        shopUI.createSavegameUI(this.player);
        shopUI.createShopTitleUI();
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
