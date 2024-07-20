use bevy::prelude::*;
use bevy_asset_loader::prelude::*;

use crate::game::shop::pets::Pets;

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
}

#[derive(AssetCollection, Resource)]
pub struct GUIAssets {
    #[asset(path = "sprites/gui/money.png")]
    pub money: Handle<Image>,

    #[asset(path = "sprites/gui/multiplier.png")]
    pub multiplier: Handle<Image>,

    #[asset(path = "sprites/gui/pets.png")]
    pub pets: Handle<Image>,

    #[asset(path = "sprites/pets", collection(typed))]
    pub pet_icons: Vec<Handle<Image>>,

    #[asset(texture_atlas_layout(tile_size_x = 64, tile_size_y = 64, columns = 4, rows = 4))]
    pub pet_icon_layout: Handle<TextureAtlasLayout>,
}

#[derive(AssetCollection, Resource)]
pub struct DataAssets {
    #[asset(path = "data/common.pets.json")]
    pub pets: Handle<Pets>,
}
