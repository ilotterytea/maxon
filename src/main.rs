use crate::systems::spawn_2d_camera;
use assets::AppAssets;
use bevy::{
    diagnostic::{FrameTimeDiagnosticsPlugin, LogDiagnosticsPlugin},
    prelude::*,
    window::{Window, WindowPlugin},
    DefaultPlugins,
};
use bevy_asset_loader::prelude::*;
use bevy_framepace::FramepacePlugin;

mod assets;
mod systems;

fn main() {
    App::new()
        .add_plugins(DefaultPlugins.set(WindowPlugin {
            primary_window: Some(Window {
                title: "Maxon Petting Simulator".into(),
                resolution: (800., 600.).into(),
                present_mode: bevy::window::PresentMode::AutoVsync,
                fit_canvas_to_parent: true,
                prevent_default_event_handling: false,
                ..default()
            }),
            ..default()
        }))
        // Game states
        .add_state::<AppState>()
        // Startup system
        .add_startup_system(spawn_2d_camera)
        // Loading state
        .add_loading_state(LoadingState::new(AppState::Boot).continue_to_state(AppState::Splash))
        .add_collection_to_loading_state::<_, AppAssets>(AppState::Boot)
        // FPS Debug
        .add_plugin(FrameTimeDiagnosticsPlugin)
        .add_plugin(LogDiagnosticsPlugin::default())
        // Framerate lock
        .add_plugin(FramepacePlugin)
        .run();
}

#[derive(Clone, Eq, PartialEq, Debug, Hash, Default, States)]
pub enum AppState {
    #[default]
    Boot,
    Splash,
    Menu,
    Game,
    Pause,
    Error,
}
