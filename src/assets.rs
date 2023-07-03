use bevy::{prelude::*, sprite::TextureAtlas};
use bevy_asset_loader::asset_collection::AssetCollection;

#[derive(AssetCollection, Resource)]
pub struct AppAssets {
    #[asset(path = "sprites/logo.png")]
    pub brand_logo: Handle<Image>,

    #[asset(texture_atlas(tile_size_x = 28., tile_size_y = 28., columns = 7, rows = 7))]
    #[asset(path = "sprites/widgets.png")]
    pub menu_widgets: Handle<TextureAtlas>,

    #[asset(path = "fonts/main.ttf")]
    pub main_font: Handle<Font>,

    #[asset(path = "fonts/bold.ttf")]
    pub bold_font: Handle<Font>,
}
