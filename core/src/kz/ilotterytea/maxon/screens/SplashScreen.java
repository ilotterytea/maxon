package kz.ilotterytea.maxon.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import kz.ilotterytea.maxon.assets.AssetUtils;
import kz.ilotterytea.maxon.MaxonGame;
import kz.ilotterytea.maxon.utils.OsUtils;

public class SplashScreen implements Screen {
    private MaxonGame game = MaxonGame.getInstance();

    private Stage stage;

    private TextureAtlas textureAtlas;

    private ProgressBar bar;

    private boolean assetsLoaded = false;

    @Override public void show() {
        this.game = MaxonGame.getInstance();

        this.stage = new Stage(new FitViewport(800, 600));
        Skin skin = new Skin(Gdx.files.internal("MainSpritesheet.skin"));

        Table logoTable = new Table();
        logoTable.setFillParent(true);
        logoTable.align(Align.center);

        textureAtlas = new TextureAtlas(Gdx.files.internal("sprites/gui/ilotterytea.atlas"));
        Image image = new Image(textureAtlas.findRegion("devOld"));

        float stageWidth;

        if (OsUtils.isMobile) {
            stageWidth = this.stage.getWidth() - 20f;
        } else {
            stageWidth = 700f;
        }

        float difference = stageWidth / image.getWidth();
        image.setSize(stageWidth, image.getHeight() * difference);

        logoTable.add(image).size(image.getWidth(), image.getHeight()).padBottom(60f).row();

        bar = new ProgressBar(0f, 100f, 1f, false, skin);
        logoTable.add(bar).width(image.getWidth());

        stage.addActor(logoTable);

        AssetUtils.setup(game.assetManager);
        AssetUtils.queue(game.assetManager);
    }

    private void update() {
        if (game.assetManager.update() && !assetsLoaded) {
            stage.addAction(
                    Actions.sequence(
                            Actions.alpha(0.0f, 1f),
                            new Action() {
                                @Override
                                public boolean act(float v) {
                                    game.getPetManager().load();
                                    game.setScreen(new MenuScreen());
                                    dispose();
                                    return false;
                                }
                            }
                    )
            );
            assetsLoaded = true;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        stage.act(delta);

        update();
        bar.setValue(100f / (game.assetManager.getQueuedAssets() + 1));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() {
        textureAtlas.dispose();
    }
}
