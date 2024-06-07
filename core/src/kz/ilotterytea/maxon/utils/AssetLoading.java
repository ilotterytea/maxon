package kz.ilotterytea.maxon.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import kz.ilotterytea.maxon.anim.SpriteUtils;
import kz.ilotterytea.maxon.player.MaxonItemEnum;
import kz.ilotterytea.maxon.player.MaxonItemRegister;
import kz.ilotterytea.maxon.ui.AnimatedImage;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class AssetLoading {
    public static void setup(AssetManager am) {
        am.setLoader(SceneAsset.class, ".glb", new GLBAssetLoader());
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

    public static void registerItems(AssetManager am, I18N i18n) {
        MaxonItemRegister.clear();

        MaxonItemRegister.register(
                0, i18n.TranslatableText("pet.bror.name"), i18n.TranslatableText("pet.bror.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/bror.png", Texture.class), 112, 112, 11, 7)),
                MaxonItemEnum.SLAVE,
                100,
                1f
        );

        MaxonItemRegister.register(
                1, i18n.TranslatableText("pet.sandwich.name"), i18n.TranslatableText("pet.sandwich.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/sandwich_cat.png", Texture.class), 112, 112, 4, 7)),
                MaxonItemEnum.SLAVE,
                1000,
                12f
        );

        MaxonItemRegister.register(
                2, i18n.TranslatableText("pet.manlooshka.name"), i18n.TranslatableText("pet.manlooshka.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/manlooshka.png", Texture.class), 112, 112, 10, 4)),
                MaxonItemEnum.SLAVE,
                5000,
                36f
        );

        MaxonItemRegister.register(
                3, i18n.TranslatableText("pet.thirsty.name"), i18n.TranslatableText("pet.thirsty.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/thirsty_cat.png", Texture.class), 112, 112, 6, 3)),
                MaxonItemEnum.SLAVE,
                10000,
                100f
        );

        MaxonItemRegister.register(
                4, i18n.TranslatableText("pet.furios.name"), i18n.TranslatableText("pet.furios.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/furios_cat.png", Texture.class), 112, 112, 7, 4)),
                MaxonItemEnum.SLAVE,
                750000,
                320f
        );

        MaxonItemRegister.register(
                5, i18n.TranslatableText("pet.tvcat.name"), i18n.TranslatableText("pet.tvcat.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/tvcat.png", Texture.class), 112, 112, 5, 5)),
                MaxonItemEnum.SLAVE,
                1500000,
                950f
        );

        MaxonItemRegister.register(
                6, i18n.TranslatableText("pet.progcat.name"), i18n.TranslatableText("pet.progcat.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/progcat.png", Texture.class), 112, 112, 7, 6)),
                MaxonItemEnum.SLAVE,
                3000000,
                2900f
        );

        MaxonItemRegister.register(
                7, i18n.TranslatableText("pet.screamcat.name"), i18n.TranslatableText("pet.screamcat.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/screamcat.png", Texture.class), 112, 112, 10, 10)),
                MaxonItemEnum.SLAVE,
                7000000,
                8700f
        );

        MaxonItemRegister.register(
                8, i18n.TranslatableText("pet.hellcat.name"), i18n.TranslatableText("pet.hellcat.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/hellcat.png", Texture.class), 128, 128, 10, 8)),
                MaxonItemEnum.SLAVE,
                13000000,
                26100f
        );

        MaxonItemRegister.register(
                9, i18n.TranslatableText("pet.lurker.name"), i18n.TranslatableText("pet.lurker.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/lurker.png", Texture.class), 112, 112, 10, 7)),
                MaxonItemEnum.SLAVE,
                20000000,
                78300f
        );

        MaxonItemRegister.register(
                10, i18n.TranslatableText("pet.piano.name"), i18n.TranslatableText("pet.piano.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/piano_cat.png", Texture.class), 128, 128, 5, 3)),
                MaxonItemEnum.SLAVE,
                40000000,
                234900f
        );

        MaxonItemRegister.register(
                11, i18n.TranslatableText("pet.bee.name"), i18n.TranslatableText("pet.bee.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/bee_cat.png", Texture.class), 112, 112, 10, 10)),
                MaxonItemEnum.SLAVE,
                70000000,
                704700f
        );

        MaxonItemRegister.register(
                12, i18n.TranslatableText("pet.busy.name"), i18n.TranslatableText("pet.busy.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/busy.png", Texture.class), 112, 112, 5, 5)),
                MaxonItemEnum.SLAVE,
                150000000,
                2114100f
        );

        MaxonItemRegister.register(
                13, i18n.TranslatableText("pet.aeae.name"), i18n.TranslatableText("pet.aeae.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/aeae.png", Texture.class), 112, 112, 11, 10)),
                MaxonItemEnum.SLAVE,
                250000000,
                6342300f
        );

        MaxonItemRegister.register(
                14, i18n.TranslatableText("pet.succat.name"), i18n.TranslatableText("pet.succat.desc"),
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/succat.png", Texture.class), 128, 128, 13, 10)),
                MaxonItemEnum.SLAVE,
                500000000,
                19026900f
        );
    }
}