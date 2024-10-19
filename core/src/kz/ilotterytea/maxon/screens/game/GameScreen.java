package kz.ilotterytea.maxon.screens.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.MaxonGame;
import kz.ilotterytea.maxon.anim.SpriteUtils;
import kz.ilotterytea.maxon.audio.Playlist;
import kz.ilotterytea.maxon.inputprocessors.CrossProcessor;
import kz.ilotterytea.maxon.pets.Pet;
import kz.ilotterytea.maxon.pets.PetManager;
import kz.ilotterytea.maxon.player.DecalPlayer;
import kz.ilotterytea.maxon.player.MaxonItem;
import kz.ilotterytea.maxon.player.MaxonItemRegister;
import kz.ilotterytea.maxon.player.Savegame;
import kz.ilotterytea.maxon.screens.MenuScreen;
import kz.ilotterytea.maxon.screens.game.shop.ShopUI;
import kz.ilotterytea.maxon.ui.*;
import kz.ilotterytea.maxon.ui.game.QuickActionsTable;
import kz.ilotterytea.maxon.utils.OsUtils;
import kz.ilotterytea.maxon.utils.math.Math;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.util.ArrayList;
import java.util.Map;

public class GameScreen implements Screen, InputProcessor {
    final MaxonGame game;
    final long playTimestamp;
    boolean isShopping = false, isInventoryEnabled = false;

    private Savegame savegame = Savegame.getInstance();

    Stage stage;
    private Skin skin, uiSkin;

    TextureAtlas mainAtlas, playerIconAtlas;

    Label pointsLabel, multiplierLabel;
    AnimatedImage cat;
    AnimatedImageButton maxon;

    Table boardTable, quickTable;

    Dialog notEnoughPointsDialog;

    ArrayList<MaxonItem> items;
    Map<Integer, Integer> invItems;

    MovingChessBackground bg;
    Playlist playlist;

    private ShopUI shopUI;

    private SceneManager sceneManager;
    private PerspectiveCamera camera;

    private DecalBatch decalBatch;
    private ArrayList<Decal> decals;
    private DecalPlayer decalPlayer;

    private float elapsedTime = 0.0f;

    private Giftbox giftbox;

    private final ArrayList<Timer.Task> tasks = new ArrayList<>();

    public GameScreen() {
        this.game = MaxonGame.getInstance();
        this.playTimestamp = System.currentTimeMillis();

        create3D();

        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
        decals = new ArrayList<>();

        ArrayList<TextureRegion> playerTextureRegions = SpriteUtils.splitToTextureRegions(game.assetManager.get("sprites/sheet/loadingCircle.png", Texture.class), 112, 112, 10, 5);
        decalPlayer = new DecalPlayer(savegame, playerTextureRegions);
        decals.add(decalPlayer.getDecal());

        playlist = new Playlist(
                game.assetManager.get("mus/game/onwards.mp3", Music.class),
                game.assetManager.get("mus/game/paris.mp3", Music.class),
                game.assetManager.get("mus/game/adieu.mp3", Music.class),
                game.assetManager.get("mus/game/shopping_spree.mp3", Music.class)
        );
        playlist.setShuffleMode(true);
        if (game.prefs.getBoolean("music", true)) playlist.next();

        items = new ArrayList<>();

        createStageUI();

        giftbox = new Giftbox(stage, uiSkin, game.assetManager, sceneManager);

        Gdx.input.setInputProcessor(new InputMultiplexer(this, new CrossProcessor(), stage));
    }

    @Override
    public void show() {
        tasks.add(Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                savegame.save();
            }
        }, 10, 10));

        // Add a 1/10th multiplier to the money every 1/10th of a second.
        tasks.add(Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                double multiplier = 0.0f;

                for (String id : savegame.getPurchasedPets().keySet()) {
                    Pet pet = game.getPetManager().getPet(id);

                    if (pet == null) {
                        continue;
                    }

                    int amount = savegame.getPurchasedPets().get(id);

                    double m = pet.getMultiplier() * amount;
                    multiplier += m;
                }

                multiplier /= 10f;

                savegame.increaseMoney(multiplier);
            }
        }, 0.1f, 0.1f));

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

        this.giftbox.update(delta, this.camera);

        this.decalPlayer.render(this.camera);

        for (Decal decal : this.decals) {
            decal.lookAt(this.camera.position, this.camera.up);
            this.decalBatch.add(decal);
        }

        // - - -  R E N D E R I N G  P E T S  - - -
        ArrayList<Decal> petDecals = new ArrayList<>();

        // Getting the pet decals
        for (String id : savegame.getPurchasedPets().keySet()) {
            PetManager petManager = game.getPetManager();
            Pet pet = petManager.getPet(id);

            if (pet == null) {
                continue;
            }

            int amount = savegame.getPurchasedPets().get(id);

            for (int i = 0; i < amount; i++) {
                Decal decal = pet.getDecal();
                petDecals.add(Decal.newDecal(decal.getWidth(), decal.getHeight(), decal.getTextureRegion()));
            }
        }

        elapsedTime += delta;

        // Rendering the pet decals
        for (int i = 0; i < petDecals.size(); i++) {
            Decal decal = petDecals.get(i);

            float angle = elapsedTime + (i * MathUtils.PI2 / petDecals.size());
            float radius = 2.0f;
            float x = MathUtils.cos(angle) * radius;
            float z = MathUtils.sin(angle) * radius;

            decal.setPosition(decalPlayer.getDecal().getX() + x, 0.5f, decalPlayer.getDecal().getZ() + z);
            decal.lookAt(this.camera.position, this.camera.up);
            this.decalBatch.add(decal);
        }

        this.decalBatch.flush();

        stage.act(delta);
        stage.draw();

        shopUI.render();
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
        sceneManager.updateViewport(width, height);
        this.shopUI.update();
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
        for (Timer.Task task : tasks) {
            task.cancel();
        }
        tasks.clear();

        playlist.getPlayingNow().stop();
        dispose();
    }

    @Override
    public void dispose() {
        giftbox.dispose();
        stage.clear();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            savegame.setElapsedTime((System.currentTimeMillis() - playTimestamp) + savegame.getElapsedTime());
            savegame.save();

            game.setScreen(new MenuScreen());
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

        savegame.increaseMoney(savegame.getMultiplier());

        final TypingLabel label = new TypingLabel(game.locale.FormattedText("game.newPoint", MaxonConstants.DECIMAL_FORMAT.format(savegame.getMultiplier())), skin, "default");

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

        tasks.add(Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                label.remove();
            }
        }, 10f));

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
        camera.position.set(-3f, 2f, -0.3f);

        float angle = 256f;

        if (OsUtils.isMobile) {
            angle = 245f;
        }

        camera.rotate(angle, 0f, 1f, 0f);

        camera.update();

        sceneManager.setCamera(camera);

        DirectionalShadowLight light = new DirectionalShadowLight(1024, 1024, 60f, 60f, 1f, 300f);
        light.set(new Color(0xdcccffff), -1f, -0.8f, -0.2f);
        light.intensity = 5f;
        sceneManager.environment.add(light);
        sceneManager.environment.shadowMap = light;

        PointLightEx signLight = new PointLightEx();
        signLight.set(Color.PINK, new Vector3(2f, 6f, 2f), 80f, 100f);

        PointLightEx windowLight = new PointLightEx();
        windowLight.set(Color.BLUE, new Vector3(-1.1f, 7.3f, 0.5f), 80f, 100f);

        sceneManager.environment.add(windowLight, signLight);

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
        this.playerIconAtlas = this.game.assetManager.get("sprites/gui/player_icons.atlas", TextureAtlas.class);

        this.uiSkin = this.game.assetManager.get("sprites/gui/ui.skin", Skin.class);

        this.shopUI = new ShopUI(savegame, this.stage, this.uiSkin, this.playerIconAtlas);

        if (OsUtils.isMobile) {
            shopUI.createSavegameUI();
        }

        shopUI.createShopTitleUI();
        shopUI.createShopControlUI();
        shopUI.createShopListUI();

        if (!OsUtils.isMobile) {
            shopUI.createSavegameUI();
        }

        DebugWidget debugWidget = new DebugWidget(uiSkin);
        this.stage.addActor(debugWidget);

        this.stage.addActor(new QuickActionsTable(this.game.assetManager.get("sprites/gui/widgets.skin", Skin.class)));
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
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
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
