use bevy::prelude::*;
use bevy_asset_loader::prelude::*;

use crate::localization::Localization;

#[derive(AssetCollection, Resource)]
pub struct AppAssets {
    #[asset(texture_atlas(tile_size_x = 112., tile_size_y = 112., columns = 10, rows = 4))]
    #[asset(path = "sprites/sheet/loadingCircle.png")]
    pub cat_maxon: Handle<TextureAtlas>,

    #[asset(path = "i18n/en_us.locale.json")]
    pub locale_english: Handle<Localization>,
}