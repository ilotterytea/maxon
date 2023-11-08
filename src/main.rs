use assets::AppAssets;
use bevy::{
    diagnostic::{FrameTimeDiagnosticsPlugin, LogDiagnosticsPlugin},
    prelude::*,
    window::PresentMode,
};
use bevy_asset_loader::prelude::*;
use bevy_common_assets::json::JsonAssetPlugin;
use game::GamePlugin;
use localization::Localization;
use startup_systems::spawn_2d_camera;

mod assets;
mod game;
mod localization;
mod settings;
mod startup_systems;
mod style;

fn main() {
    App::new()
        .add_plugins((
            DefaultPlugins.set(WindowPlugin {
                primary_window: Some(Window {
                    title: "Maxon Petting Simulator".into(),
                    present_mode: PresentMode::AutoVsync,
                    ..default()
                }),
                ..default()
            }),
            JsonAssetPlugin::<Localization>::new(&["locale.json"]),
        ))
        // App states
        .add_state::<AppState>()
        // Initializing startup systems
        .add_systems(Startup, spawn_2d_camera)
        // Loading state
        .add_loading_state(LoadingState::new(AppState::Boot).continue_to_state(AppState::Game))
        .add_collection_to_loading_state::<_, AppAssets>(AppState::Boot)
        .add_plugins(GamePlugin)
        // Diagnostics
        .add_plugins((LogDiagnosticsPlugin::default(), FrameTimeDiagnosticsPlugin))
        .run();
}

#[derive(Clone, Eq, PartialEq, Debug, Hash, Default, States)]
pub enum AppState {
    #[default]
    Boot,
    Menu,
    Game,
    Pause,
}
