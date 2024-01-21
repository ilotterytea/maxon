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

    // - - - UI  S P R I T E S - - -
    #[asset(texture_atlas(tile_size_x = 256., tile_size_y = 256., columns = 3, rows = 3))]
    #[asset(path = "sprites/gui/bar_level.png")]
    pub ui_bar_level: Handle<TextureAtlas>,

    #[asset(path = "sprites/gui/btn_fatigue.png")]
    pub ui_btn_fatigue: Handle<Image>,

    #[asset(path = "sprites/gui/btn_happiness.png")]
    pub ui_btn_happiness: Handle<Image>,

    #[asset(path = "sprites/gui/btn_hunger.png")]
    pub ui_btn_hunger: Handle<Image>,

    // - - - S C E N E S - - -
    #[asset(path = "models/scenes/mdl_maxon_room.glb#Scene0")]
    pub mdl_maxon_room: Handle<Scene>,

    #[asset(path = "models/scenes/mdl_basement.glb#Scene0")]
    pub mdl_basement_room: Handle<Scene>,

    // - - - M O D E L S - - -
    #[asset(path = "models/mdl_petbed.glb#Scene0")]
    pub mdl_petbed: Handle<Scene>,

    // - - - B U I L D I N G S - - -
    #[asset(path = "sprites/buildings/bedroom_background.png")]
    pub building_bedroom_background: Handle<Image>,

    #[asset(path = "sprites/buildings/kitchen_background.png")]
    pub building_kitchen_background: Handle<Image>,
}
