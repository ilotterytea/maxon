package kz.ilotterytea.maxoning;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import kz.ilotterytea.maxoning.localization.LocalizationManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MaxoningGame extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private AssetManager assetManager;
    private LocalizationManager localizationManager;
    private static MaxoningGame instance;

    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public LocalizationManager getLocalizationManager() {
        return localizationManager;
    }

    public static MaxoningGame getInstance() {
        return instance;
    }

    public MaxoningGame() {
        instance = this;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assetManager = new AssetManager();

        localizationManager = new LocalizationManager();
        localizationManager.loadLocalizations("l10n");

        setScreen(new FirstScreen());
    }
}