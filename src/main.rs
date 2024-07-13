use assets::{ModelAssets, TextureAtlasAssets};
use bevy::prelude::*;
use bevy_asset_loader::loading_state::{
    config::ConfigureLoadingState, LoadingState, LoadingStateAppExt,
};
use bevy_sprite3d::Sprite3dPlugin;
use game::GamePlugin;

mod assets;
mod game;
mod systems;

fn main() {
    let mut app = App::new();

    app.add_plugins(DefaultPlugins.set(WindowPlugin {
        primary_window: Some(Window {
            title: "Maxon Petting Simulator".into(),
            resolution: (800., 600.).into(),
            ..default()
        }),
        ..default()
    }));

    app.init_state::<AppState>();

    // Game plugins
    app.add_plugins(GamePlugin);

    // Asset loading
    app.add_loading_state(
        LoadingState::new(AppState::Boot)
            .continue_to_state(AppState::Game)
            .load_collection::<ModelAssets>()
            .load_collection::<TextureAtlasAssets>(),
    );

    // Startup systems
    app.add_systems(Startup, systems::setup_camera);

    // Billboard
    app.add_plugins(Sprite3dPlugin);

    #[cfg(feature = "debug")]
    {
        app.add_plugins((
            // Diagnostics
            bevy::diagnostic::FrameTimeDiagnosticsPlugin,
            bevy::diagnostic::LogDiagnosticsPlugin::default(),
            // World inspector
            bevy_inspector_egui::quick::WorldInspectorPlugin::default(),
            // Flycam
            bevy_flycam::NoCameraPlayerPlugin,
        ));
    }

    app.run();
}

#[derive(Clone, PartialEq, Eq, Hash, Debug, Default, States)]
pub enum AppState {
    #[default]
    Boot,
    Game,
}
