package kz.ilotterytea.maxon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import kz.ilotterytea.maxon.pets.PetManager;
import kz.ilotterytea.maxon.screens.SplashScreen;
import kz.ilotterytea.maxon.utils.GameUpdater;
import kz.ilotterytea.maxon.utils.I18N;

public class MaxonGame extends Game {
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	public AssetManager assetManager;
	public Preferences prefs;
	public I18N locale;

	private PetManager petManager;

	private static MaxonGame instance;

	public static MaxonGame getInstance() {
		if (instance == null) {
			instance = new MaxonGame();
		}
		return instance;
	}

	public PetManager getPetManager() {
		return petManager;
	}

	@Override
	public void create () {
		// Check the latest version
		new GameUpdater().checkLatestUpdate();

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		prefs = Gdx.app.getPreferences(MaxonConstants.GAME_APP_PACKAGE);
		locale = new I18N(Gdx.files.internal("i18n/" + prefs.getString("lang", "en_us") + ".json"));

		Gdx.graphics.setVSync(prefs.getBoolean("vsync", true));

		if (prefs.getBoolean("fullscreen", false)) {
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else if (
				prefs.contains("width") ||
				prefs.contains("height")
		) {
			int width = prefs.getInteger("width", 800);

			if (width < 800) {
				width = 800;
				prefs.putInteger("width", width);
			}

			int height = prefs.getInteger("height", 600);

			if (height < 600) {
				height = 600;
				prefs.putInteger("height", height);
			}

			prefs.flush();
			Gdx.graphics.setWindowedMode(width, height);
		}

		assetManager = new AssetManager();
		petManager = new PetManager(assetManager);

		this.setScreen(new SplashScreen());
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		for (String name : assetManager.getAssetNames()) {
			assetManager.unload(name);
		}
		assetManager.dispose();
		instance.dispose();
	}
}
