use animation::TextureAtlasAnimationPlugin;
use assets::*;
use bevy::prelude::*;
use bevy_asset_loader::loading_state::{
    config::ConfigureLoadingState, LoadingState, LoadingStateAppExt,
};
use bevy_common_assets::json::JsonAssetPlugin;
use bevy_mod_picking::DefaultPickingPlugins;
use bevy_sprite3d::Sprite3dPlugin;
use game::{shop::pets::Pets, GamePlugin};

mod animation;
mod assets;
mod constants;
mod game;
mod persistent;
mod style;
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

    // JSON loading
    app.add_plugins(JsonAssetPlugin::<Pets>::new(&["pets.json"]));

    // Asset loading
    app.add_loading_state(
        LoadingState::new(AppState::Boot)
            .continue_to_state(AppState::Game)
            .load_collection::<ModelAssets>()
            .load_collection::<TextureAtlasAssets>()
            .load_collection::<FontAssets>()
            .load_collection::<GUIAssets>()
            .load_collection::<DataAssets>(),
    );

    // Startup systems
    app.add_systems(
        Startup,
        (
            systems::setup_camera,
            persistent::setup_persistent_resources,
        ),
    );

    // Billboard
    app.add_plugins(Sprite3dPlugin);

    // 3D picking
    app.add_plugins(DefaultPickingPlugins);

    // Animation for sprite sheets
    app.add_plugins(TextureAtlasAnimationPlugin);

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
