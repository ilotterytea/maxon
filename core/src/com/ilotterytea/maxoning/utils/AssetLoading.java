package com.ilotterytea.maxoning.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.ilotterytea.maxoning.anim.SpriteUtils;
import com.ilotterytea.maxoning.player.MaxonItemEnum;
import com.ilotterytea.maxoning.player.MaxonItemRegister;
import com.ilotterytea.maxoning.ui.AnimatedImage;

public class AssetLoading {
    public static void queue(AssetManager am) {

        // Textures:
        am.load("sprites/supadank.png", Texture.class);

        am.load("sprites/sheet/loadingCircle.png", Texture.class);
        am.load("sprites/sheet/bror.png", Texture.class);
        am.load("sprites/sheet/manlooshka.png", Texture.class);
        am.load("sprites/sheet/furios_cat.png", Texture.class);
        am.load("sprites/sheet/sandwich_cat.png", Texture.class);
        am.load("sprites/sheet/thirsty_cat.png", Texture.class);

        am.load("sprites/black.png", Texture.class);
        am.load("sprites/brand.png", Texture.class);
        am.load("sprites/ilotterytea.png", Texture.class);

        am.load("sprites/menu/tile_1.png", Texture.class);
        am.load("sprites/menu/tile_2.png", Texture.class);

        // // Ninepatches:
        am.load("sprites/ui/button.9.png", Texture.class);
        am.load("sprites/ui/button_clicked.9.png", Texture.class);
        am.load("sprites/ui/button_over.9.png", Texture.class);

        // Music:
        am.load("mus/menu/mus_menu_intro.ogg", Music.class);
        am.load("mus/menu/mus_menu_loop.ogg", Music.class);
        // Sounds:
    }

    public static void registerItems(AssetManager am) {
        MaxonItemRegister.register(
                0, "The Suspicious and Sleepy Bro", "A falling asleep Bror will help you to pet Maxon almost to besvimers.",
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/bror.png", Texture.class), 112, 112, 11, 7)),
                MaxonItemEnum.SLAVE,
                300,
                0.1f
        );

        MaxonItemRegister.register(
                1, "The Sandwich Cat", "Even though his head is shielded from the light by bread, he can still to pet Maxon by his cheeks",
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/sandwich_cat.png", Texture.class), 112, 112, 4, 7)),
                MaxonItemEnum.SLAVE,
                2000,
                0.5f
        );

        MaxonItemRegister.register(
                2, "Manlooshka", "rrrrr",
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/manlooshka.png", Texture.class), 112, 112, 10, 4)),
                MaxonItemEnum.SLAVE,
                6200,
                1f
        );

        MaxonItemRegister.register(
                3, "The Thirsty Cat", "Every time the kitty drinks water, drops of spilled water fall on the screen and pet Maxon's dry cheeks.",
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/thirsty_cat.png", Texture.class), 112, 112, 6, 3)),
                MaxonItemEnum.SLAVE,
                10000,
                1.5f
        );

        MaxonItemRegister.register(
                4, "The Furios Cat", "Petting FURIOSLY !!!",
                new AnimatedImage(SpriteUtils.splitToTextureRegions(am.get("sprites/sheet/furios_cat.png", Texture.class), 112, 112, 7, 4)),
                MaxonItemEnum.SLAVE,
                20000,
                5f
        );
    }
}
