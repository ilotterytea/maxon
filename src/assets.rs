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

    #[asset(texture_atlas(tile_size_x = 64., tile_size_y = 64., columns = 15, rows = 5))]
    #[asset(path = "sprites/sheet/sleepy.png")]
    pub cat_sleepy: Handle<TextureAtlas>,

    #[asset(path = "icon.png")]
    pub icon: Handle<Image>,

    #[asset(path = "i18n/en_us.locale.json")]
    pub locale_english: Handle<Localization>,

    // - - - M O D E L S - - -
    #[asset(path = "models/scenes/mdl_maxon_room.glb")]
    pub mdl_maxon_room: Handle<Scene>,

    // - - - B U I L D I N G S - - -
    #[asset(path = "sprites/buildings/bedroom_background.png")]
    pub building_bedroom_background: Handle<Image>,

    #[asset(path = "sprites/buildings/kitchen_background.png")]
    pub building_kitchen_background: Handle<Image>,
}
