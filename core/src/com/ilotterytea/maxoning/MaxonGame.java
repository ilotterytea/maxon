package com.ilotterytea.maxoning;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ilotterytea.maxoning.player.MaxonPlayer;
import com.ilotterytea.maxoning.screens.AssetLoadingScreen;
import com.ilotterytea.maxoning.utils.serialization.GameDataSystem;

import java.io.IOException;

public class MaxonGame extends Game {
	public SpriteBatch batch;
	public AssetManager assetManager;
	public Preferences prefs;

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
		prefs = Gdx.app.getPreferences("Maxoning");

		if (!GameDataSystem.exists()) {
			try {
				GameDataSystem.SaveData(new MaxonPlayer());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		assetManager = new AssetManager();

		this.setScreen(new AssetLoadingScreen(this));
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
