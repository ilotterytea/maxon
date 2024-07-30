use bevy::prelude::*;
use bevy_asset_loader::prelude::*;

use crate::{game::shop::pets::Pets, localization::Localization};

#[derive(AssetCollection, Resource)]
pub struct ModelAssets {
    #[asset(path = "models/scenes/living_room.glb#Scene0")]
    pub living_room: Handle<Scene>,
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
}
