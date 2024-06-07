package kz.ilotterytea.maxon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.ilotterytea.maxon.MaxonGame;
import kz.ilotterytea.maxon.player.MaxonSavegame;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.io.IOException;

public class WelcomeScreen implements Screen {
    private final MaxonGame game = MaxonGame.getInstance();

    private Stage stage;

    private PerspectiveCamera camera;
    private SceneManager sceneManager;

    private Image tintImage;

    private BoundingBox collisionBox;

    private boolean boxOpened = false;

    @Override
    public void show() {
        // Setting up the scene
        sceneManager = new SceneManager();

        SceneAsset sceneAsset = game.assetManager.get("models/scenes/living_room.glb", SceneAsset.class);
        Scene scene = new Scene(sceneAsset.scene);
        sceneManager.addScene(scene);

        SceneAsset boxAsset = game.assetManager.get("models/props/box.glb", SceneAsset.class);
        Scene box = new Scene(boxAsset.scene);
        box.modelInstance.transform.setTranslation(new Vector3(2.0f, 0.25f, 1.0f));
        box.modelInstance.transform.rotate(Vector3.Y, 45);
        box.modelInstance.transform.scale(2.0f, 2.0f, 2.0f);
        sceneManager.addScene(box);

        collisionBox = new BoundingBox(
                new Vector3(-1f, 0.25f, -0.35f),
                new Vector3(1f, 2.08f, 0.8f)
        );

        // Setting up 3D view
        this.camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 300f;
        camera.position.set(-3f, 2f, -0.3f);
        camera.rotate(256f, 0f, 1f, 0f);

        camera.update();

        sceneManager.setCamera(camera);

        // Setting up the lights
        DirectionalShadowLight light = new DirectionalShadowLight(1024, 1024, 60f, 60f, 1f, 300f);
        light.set(new Color(0xdcccffff), -1f, -0.8f, -0.2f);
        light.intensity = 5f;
        sceneManager.environment.add(light);
        sceneManager.environment.shadowMap = light;

        PointLightEx windowLight = new PointLightEx();
        windowLight.set(Color.BLUE, new Vector3(-1.1f, 7.3f, 0.5f), 80f, 100f);

        PointLightEx boxLight = new PointLightEx();
        boxLight.set(Color.WHITE, new Vector3(-3f, 4f, -0.3f), 50f, 50f);

        sceneManager.environment.add(windowLight, boxLight);

        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        Cubemap diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        Cubemap specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        Texture brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // Setting up 2D UI
        stage = new Stage(new ScreenViewport());
        Skin skin = game.assetManager.get("sprites/gui/ui.skin", Skin.class);

        Table table = new Table();
        table.setFillParent(true);
        table.align(Align.center | Align.bottom);

        Table labelTable = new Table(skin);
        labelTable.setBackground("bg");

        Label label = new Label("Press the box to unpack...", skin);
        labelTable.add(label).pad(25f, 35f, 25f, 35f);

        labelTable.addAction(Actions.alpha(0.5f));
        label.addAction(
                Actions.repeat(
                        RepeatAction.FOREVER,
                        Actions.sequence(
                                Actions.alpha(0.0f, 0.5f),
                                Actions.delay(0.2f),
                                Actions.alpha(1.0f, 0.5f),
                                Actions.delay(0.2f)
                        )
                )
        );

        table.add(labelTable).padBottom(25f);

        stage.addActor(table);

        // Tint
        TextureAtlas atlas = game.assetManager.get("sprites/gui/ui.atlas", TextureAtlas.class);
        tintImage = new Image(atlas.findRegion("tile"));
        tintImage.setFillParent(true);

        tintImage.addAction(Actions.alpha(0.0f, 1.0f));

        stage.addActor(tintImage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        sceneManager.update(delta);
        sceneManager.render();

        stage.act(delta);
        stage.draw();

        Ray ray = null;

        if (Gdx.input.justTouched()) {
            ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        }

        if (ray == null) {
            return;
        }

        Vector3 intersection = new Vector3();

        if (Intersector.intersectRayBounds(ray, collisionBox, intersection) && !boxOpened) {
            boxOpened = true;
            tintImage.addAction(
                    Actions.sequence(
                            Actions.alpha(1.0f, 0.5f),
                            Actions.delay(1.0f),
                            new Action() {
                                @Override
                                public boolean act(float delta) {
                                    try {
                                        game.setScreen(new GameScreen(game, new MaxonSavegame(), 0));
                                    } catch (IOException | ClassNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return true;
                                }
                            }
                    )
            );
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        sceneManager.updateViewport(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        stage.dispose();
    }
}
