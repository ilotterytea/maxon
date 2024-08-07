use bevy::prelude::*;
use bevy_asset_loader::prelude::*;

use crate::{game::shop::pets::Pets, localization::Localization};

#[derive(AssetCollection, Resource)]
pub struct ModelAssets {
    #[asset(path = "models/scenes/living_room.glb#Scene0")]
    pub living_room: Handle<Scene>,

    #[asset(path = "models/props/chest.glb#Scene0")]
    pub chest_prop: Handle<Scene>,

    // this could be done with #Animation0 for chest i guess,
    // but idc about that and i want to get things done faster
    #[asset(path = "models/props/chest_opened.glb#Scene0")]
    pub chest_opened_prop: Handle<Scene>,
}

#[derive(AssetCollection, Resource)]
pub struct TextureAtlasAssets {
    #[asset(texture_atlas_layout(tile_size_x = 112, tile_size_y = 112, columns = 10, rows = 5))]
    pub player_layout: Handle<TextureAtlasLayout>,

    #[asset(path = "sprites/sheet/loadingCircle.png")]
    pub player_texture: Handle<Image>,
}

#[derive(AssetCollection, Resource)]
pub struct FontAssets {
    #[asset(path = "fonts/text.ttf")]
    pub text: Handle<Font>,

    #[asset(path = "fonts/debug.ttf")]
    pub debug: Handle<Font>,
}

#[derive(AssetCollection, Resource)]
pub struct GUIAssets {
    #[asset(path = "sprites/gui/money.png")]
    pub money: Handle<Image>,

    #[asset(path = "sprites/gui/multiplier.png")]
    pub multiplier: Handle<Image>,

    #[asset(path = "sprites/gui/pets.png")]
    pub pets: Handle<Image>,

    #[asset(path = "sprites/gui/logo.png")]
    pub logo: Handle<Image>,

    #[asset(path = "sprites/gui/exit.png")]
    pub exit: Handle<Image>,

    #[asset(path = "sprites/gui/music_on.png")]
    pub music_on: Handle<Image>,

    #[asset(path = "sprites/gui/music_off.png")]
    pub music_off: Handle<Image>,

    #[asset(path = "sprites/gui/fullscreen.png")]
    pub fullscreen: Handle<Image>,

    #[asset(path = "sprites/gui/windowed.png")]
    pub windowed: Handle<Image>,

    #[asset(path = "sprites/gui/languages", collection(typed))]
    pub languages: Vec<Handle<Image>>,

    #[asset(path = "sprites/pets", collection(typed))]
    pub pet_icons: Vec<Handle<Image>>,

    #[asset(texture_atlas_layout(tile_size_x = 64, tile_size_y = 64, columns = 4, rows = 4))]
    pub pet_icon_layout: Handle<TextureAtlasLayout>,

    #[asset(path = "sprites/gui/keyboard/key_f.png")]
    pub key_f: Handle<Image>,

    #[asset(path = "sprites/gui/keyboard/key_r.png")]
    pub key_r: Handle<Image>,

    #[asset(path = "sprites/gui/keyboard/key_space.png")]
    pub key_space: Handle<Image>,
}

#[derive(AssetCollection, Resource)]
pub struct SpriteAssets {
    #[asset(path = "sprites/env/rays.png")]
    pub rays: Handle<Image>,

    #[asset(path = "sprites/minigames/pc_background.png")]
    pub pc_background: Handle<Image>,

    #[asset(path = "sprites/minigames/icons/runner.png")]
    pub runner_icon: Handle<Image>,

    #[asset(path = "sprites/minigames/runner/player.png")]
    pub runner_player: Handle<Image>,

    #[asset(texture_atlas_layout(tile_size_x = 16, tile_size_y = 36, columns = 12, rows = 1))]
    pub runner_piston_layout: Handle<TextureAtlasLayout>,

    #[asset(path = "sprites/minigames/runner/pistons.png")]
    pub runner_piston_texture: Handle<Image>,

    #[asset(path = "sprites/minigames/runner/background.png")]
    pub runner_background: Handle<Image>,

    #[asset(path = "sprites/minigames/runner/ground.png")]
    pub runner_ground: Handle<Image>,
}

#[derive(AssetCollection, Resource)]
pub struct DataAssets {
    #[asset(path = "data/common.pets.json")]
    pub pets: Handle<Pets>,

    #[asset(path = "i18n", collection(typed))]
    pub localizations: Vec<Handle<Localization>>,
}

#[derive(AssetCollection, Resource)]
pub struct MusicAssets {
    #[asset(path = "mus/game", collection(typed))]
    pub game: Vec<Handle<AudioSource>>,

    #[asset(path = "mus/menu/mus_menu_loop.ogg")]
    pub menu: Handle<AudioSource>,
}

#[derive(AssetCollection, Resource)]
pub struct SFXAssets {
    #[asset(path = "sfx/player/purr.ogg")]
    pub purr: Handle<AudioSource>,

    #[asset(path = "sfx/shop/not_enough_money.ogg")]
    pub not_enough_money: Handle<AudioSource>,

    #[asset(path = "sfx/shop/purchase.ogg")]
    pub purchase: Handle<AudioSource>,

    #[asset(path = "sfx/shop/sell.ogg")]
    pub sell: Handle<AudioSource>,

    #[asset(path = "sfx/ui/click.ogg")]
    pub click: Handle<AudioSource>,

    #[asset(path = "sfx/chest/chest_opened.ogg")]
    pub chest_opened: Handle<AudioSource>,

    #[asset(path = "sfx/chest/chest_click.ogg")]
    pub chest_click: Handle<AudioSource>,
}
