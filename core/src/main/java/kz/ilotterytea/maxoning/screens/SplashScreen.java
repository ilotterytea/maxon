package kz.ilotterytea.maxoning.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import kz.ilotterytea.maxoning.MaxoningGame;
import kz.ilotterytea.maxoning.utils.LoadUtils;

public class SplashScreen implements Screen {
    private final MaxoningGame GAME = MaxoningGame.getInstance();

    private Stage stage;

    private Texture logoTexture;
    private Image logoImage;

    private float progress = 0f;

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        logoTexture = new Texture(Gdx.files.internal("sprites/logo.png"));
        logoImage = new Image(logoTexture);
        logoImage.setPosition(
                stage.getWidth() / 2f - logoImage.getWidth() / 2f,
                stage.getHeight() / 2f - logoImage.getHeight() / 2f
        );

        stage.addActor(logoImage);

        LoadUtils.queueAssets(GAME.getAssetManager());
    }

    private void update() {
        if (GAME.getAssetManager().update(2500)) {
            Gdx.app.log("SplashScreen", "ASSETS LOADED!");

            GAME.setScreen(new MenuScreen());
        }

        progress = logoImage.getWidth() / (GAME.getAssetManager().getQueuedAssets() + 1f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        ShapeRenderer shapeRenderer = GAME.getShapeRenderer();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(
                progress,
                logoImage.getY() - 72f,
                logoImage.getWidth() - progress,
                12f
        );

        // Foreground
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.rect(
                logoImage.getX(),
                logoImage.getY() - 72f,
                progress,
                12f
        );

        shapeRenderer.end();

        update();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        logoTexture.dispose();
    }
}
