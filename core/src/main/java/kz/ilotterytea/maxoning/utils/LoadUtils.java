package kz.ilotterytea.maxoning.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LoadUtils {
    public static void queueAssets(AssetManager assetManager) {
        // Texture atlases
        assetManager.load("main_spritesheet.atlas", TextureAtlas.class);

        // Skin
        assetManager.load("main_spritesheet.skin", Skin.class, new SkinLoader.SkinParameter("main_spritesheet.atlas"));

        // Textures
        assetManager.load("sprites/logo.png", Texture.class);
    }
}
