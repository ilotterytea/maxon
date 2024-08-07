use animation::TextureAtlasAnimationPlugin;
use assets::*;
use bevy::prelude::*;
use bevy_asset_loader::loading_state::{
    config::ConfigureLoadingState, LoadingState, LoadingStateAppExt,
};
use bevy_common_assets::json::JsonAssetPlugin;
use bevy_mod_billboard::plugin::BillboardPlugin;
use bevy_mod_picking::DefaultPickingPlugins;
use bevy_simple_scroll_view::ScrollViewPlugin;
use bevy_sprite3d::Sprite3dPlugin;
use bevy_tweening::TweeningPlugin;
use boot::BootPlugin;
use game::{shop::pets::Pets, GamePlugin};
use localization::Localization;
use menu::MenuPlugin;

mod animation;
mod assets;
mod boot;
mod constants;
mod debug;
mod discord;
mod game;
mod localization;
mod menu;
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
    app.add_plugins((BootPlugin, MenuPlugin, GamePlugin));

    // JSON loading
    app.add_plugins(JsonAssetPlugin::<Pets>::new(&["pets.json"]))
        .add_plugins(JsonAssetPlugin::<Localization>::new(&["locale.json"]));

    // Asset loading
    app.add_loading_state(
        LoadingState::new(AppState::AssetLoading)
            .continue_to_state(AppState::Boot)
            .load_collection::<ModelAssets>()
            .load_collection::<TextureAtlasAssets>()
            .load_collection::<FontAssets>()
            .load_collection::<GUIAssets>()
            .load_collection::<DataAssets>()
            .load_collection::<MusicAssets>()
            .load_collection::<SFXAssets>()
            .load_collection::<SpriteAssets>(),
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
    app.add_plugins((Sprite3dPlugin, BillboardPlugin));

    // Discord presence
    app.add_systems(Startup, discord::init_discord_ipc_client);
    app.add_systems(Update, discord::update_discord_ipc_client);
    app.add_systems(
        Last,
        discord::shutdown_discord_ipc_client.run_if(on_event::<AppExit>()),
    );

    // 3D picking
    app.add_plugins(DefaultPickingPlugins);

    // Scrolling content
    app.add_plugins(ScrollViewPlugin);

    // Animation for sprite sheets
    app.add_plugins(TextureAtlasAnimationPlugin);

    // Tweening
    app.add_plugins(TweeningPlugin);

    #[cfg(feature = "debug")]
    {
        app.add_plugins((
            // Diagnostics
            debug::DebugPlugin,
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
    AssetLoading,
    Boot,
    Menu,
    Game,
    MinigamesLobby,
    Minigame,
}
