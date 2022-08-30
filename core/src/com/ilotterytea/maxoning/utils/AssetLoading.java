package com.ilotterytea.maxoning.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

public class AssetLoading {
    public static void queue(AssetManager am) {

        // Textures:
        am.load("icon.png", Texture.class);
        am.load("dev.png", Texture.class);

        am.load("sprites/sheet/loadingCircle.png", Texture.class);
        am.load("sprites/sheet/bror.png", Texture.class);
        am.load("sprites/sheet/manlooshka.png", Texture.class);
        am.load("sprites/sheet/tvcat.png", Texture.class);

        am.load("sprites/black.png", Texture.class);
        am.load("sprites/white.png", Texture.class);
        am.load("sprites/brand.png", Texture.class);
        am.load("sprites/ilotterytea.png", Texture.class);
        am.load("sprites/SplashWall.png", Texture.class);

        am.load("sprites/menu/tile_cat.png", Texture.class);
        am.load("sprites/menu/tile_paw.png", Texture.class);

        // // Ninepatches:
        am.load("sprites/ui/save_slot.9.png", Texture.class);
        am.load("sprites/ui/save_slot_disabled.9.png", Texture.class);
        am.load("sprites/ui/button_static.9.png", Texture.class);
        am.load("sprites/ui/button_pressed.9.png", Texture.class);
        am.load("sprites/ui/button_highlighted.9.png", Texture.class);
        am.load("sprites/ui/button.9.png", Texture.class);
        am.load("sprites/ui/button_clicked.9.png", Texture.class);
        am.load("sprites/ui/button_over.9.png", Texture.class);

        // Music:
        am.load("mus/menu/mus_menu_intro.ogg", Music.class);
        am.load("mus/menu/mus_menu_loop.ogg", Music.class);
        // Sounds:
    }
}
