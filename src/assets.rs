use bevy::prelude::*;
use bevy_asset_loader::prelude::*;

use crate::localization::Localization;

#[derive(AssetCollection, Resource)]
pub struct AppAssets {
    #[asset(path = "fonts/font_text.ttf")]
    pub font_text: Handle<Font>,

    #[asset(texture_atlas(tile_size_x = 112., tile_size_y = 112., columns = 10, rows = 4))]
    #[asset(path = "sprites/sheet/loadingCircle.png")]
    pub cat_maxon: Handle<TextureAtlas>,

    #[asset(path = "icon.png")]
    pub icon: Handle<Image>,

    #[asset(path = "i18n/en_us.locale.json")]
    pub locale_english: Handle<Localization>,

    // - - - B U I L D I N G S - - -
    #[asset(path = "sprites/buildings/bedroom_background.png")]
    pub building_bedroom_background: Handle<Image>,
}
