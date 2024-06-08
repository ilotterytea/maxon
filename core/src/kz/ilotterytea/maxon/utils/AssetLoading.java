package kz.ilotterytea.maxon.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import kz.ilotterytea.maxon.assets.loaders.Text;
import kz.ilotterytea.maxon.assets.loaders.TextLoader;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class AssetLoading {
    public static void setup(AssetManager am) {
        am.setLoader(SceneAsset.class, ".glb", new GLBAssetLoader());
        am.setLoader(Text.class, new TextLoader(new InternalFileHandleResolver()));
    }

    public static void queue(AssetManager am) {
        // Texture atlases:
        am.load("sprites/env/environment.atlas", TextureAtlas.class);
        am.load("sprites/gui/brand.atlas", TextureAtlas.class);
        am.load("sprites/gui/icons.atlas", TextureAtlas.class);
        am.load("sprites/gui/ilotterytea.atlas", TextureAtlas.class);
        am.load("sprites/gui/widgets.atlas", TextureAtlas.class);
        am.load("sprites/gui/widgets.skin", Skin.class, new SkinLoader.SkinParameter("sprites/gui/widgets.atlas"));

        am.load("sprites/gui/widgeticons.atlas", TextureAtlas.class);
        am.load("sprites/gui/friends.atlas", TextureAtlas.class);
        am.load("sprites/gui/friends.skin", Skin.class, new SkinLoader.SkinParameter("sprites/gui/friends.atlas"));

        am.load("MainSpritesheet.atlas", TextureAtlas.class);
        am.load("MainSpritesheet.skin", Skin.class, new SkinLoader.SkinParameter("MainSpritesheet.atlas"));

        am.load("sprites/gui/ui.atlas", TextureAtlas.class);
        am.load("sprites/gui/ui.skin", Skin.class, new SkinLoader.SkinParameter("sprites/gui/ui.atlas"));

        am.load("sprites/gui/player_icons.atlas", TextureAtlas.class);

        // Models:
        am.load("models/scenes/living_room.glb", SceneAsset.class);
        am.load("models/props/box.glb", SceneAsset.class);

        // Cat item textures:
        am.load("sprites/sheet/loadingCircle.png", Texture.class);
        am.load("sprites/sheet/bror.png", Texture.class);
        am.load("sprites/sheet/manlooshka.png", Texture.class);
        am.load("sprites/sheet/furios_cat.png", Texture.class);
        am.load("sprites/sheet/sandwich_cat.png", Texture.class);
        am.load("sprites/sheet/thirsty_cat.png", Texture.class);
        am.load("sprites/sheet/tvcat.png", Texture.class); 
        am.load("sprites/sheet/progcat.png", Texture.class); 
        am.load("sprites/sheet/screamcat.png", Texture.class); 
        am.load("sprites/sheet/hellcat.png", Texture.class); 
        am.load("sprites/sheet/lurker.png", Texture.class); 
        am.load("sprites/sheet/piano_cat.png", Texture.class); 
        am.load("sprites/sheet/bee_cat.png", Texture.class); 
        am.load("sprites/sheet/busy.png", Texture.class); 
        am.load("sprites/sheet/aeae.png", Texture.class); 
        am.load("sprites/sheet/succat.png", Texture.class); 

        // Music:
        am.load("mus/menu/mus_menu_intro.ogg", Music.class);
        am.load("mus/menu/mus_menu_loop.ogg", Music.class);
        am.load("mus/game/onwards.wav", Music.class);
        am.load("mus/game/paris.wav", Music.class);
        am.load("mus/game/adieu.wav", Music.class);
        am.load("mus/game/shopping_spree.wav", Music.class);
        // Sounds:
    }
}