package com.ilotterytea.maxoning;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ilotterytea.maxoning.player.MaxonPlayer;
import com.ilotterytea.maxoning.screens.SplashScreen;
import com.ilotterytea.maxoning.utils.I18N;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;

import java.io.IOException;

public class MaxonGame extends Game {
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	public AssetManager assetManager;
	public Preferences prefs;
	public I18N locale;

	private static MaxonGame instance;

	public static MaxonGame getInstance() {
		if (instance == null) {
			instance = new MaxonGame();
		}
		return instance;
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		prefs = Gdx.app.getPreferences("Maxoning");
		locale = new I18N(Gdx.files.internal("i18n/" + prefs.getString("lang", "en_us") + ".json"));

		if (!GameDataSystem.exists()) {
			try {
				GameDataSystem.SaveData(new MaxonPlayer());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		prefs.putInteger("width", Gdx.graphics.getWidth());
		prefs.putInteger("height", Gdx.graphics.getHeight());
		prefs.flush();

		Gdx.graphics.setVSync(prefs.getBoolean("vsync", true));
		if (prefs.getBoolean("fullscreen", false)) { Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode()); }

		assetManager = new AssetManager();

		this.setScreen(new SplashScreen(this));
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
