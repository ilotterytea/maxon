use bevy::prelude::*;
use bevy_asset_loader::asset_collection::AssetCollection;

#[derive(AssetCollection, Resource)]
pub struct AppAssets {
    // - - -  L O G O  - - -
    #[asset(path = "sprites/logo.png")]
    pub brand_logo: Handle<Image>,

    // - - -  U I  A S S E T S  - - -
    #[asset(path = "sprites/ui/music_on.png")]
    pub music_on_widget: Handle<Image>,

    #[asset(path = "sprites/ui/music_off.png")]
    pub music_off_widget: Handle<Image>,

    #[asset(path = "sprites/ui/quit.png")]
    pub quit_widget: Handle<Image>,

    #[asset(path = "sprites/ui/fullscreen.png")]
    pub fullscreen_widget: Handle<Image>,

    #[asset(path = "sprites/ui/windowed.png")]
    pub windowed_widget: Handle<Image>,

    #[asset(path = "sprites/ui/russian_flag.png")]
    pub russian_flag_widget: Handle<Image>,

    #[asset(path = "sprites/ui/usa_flag.png")]
    pub usa_flag_widget: Handle<Image>,

    // - - -  F O N T S  - - -
    #[asset(path = "fonts/main.ttf")]
    pub main_font: Handle<Font>,

    #[asset(path = "fonts/bold.ttf")]
    pub bold_font: Handle<Font>,
}
