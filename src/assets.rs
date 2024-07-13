use bevy::prelude::*;
use bevy_asset_loader::prelude::*;

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
