package com.ilotterytea.maxoning;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ilotterytea.maxoning.screen.AssetLoadingScreen;
import com.ilotterytea.maxoning.screen.SplashScreen;

public class MaxonGame extends Game {
	public SpriteBatch batch;
	public AssetManager assetManager;

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
