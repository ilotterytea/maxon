use bevy::{
    diagnostic::{FrameTimeDiagnosticsPlugin, LogDiagnosticsPlugin},
    prelude::*,
    window::{Window, WindowPlugin},
    DefaultPlugins,
};
use bevy_framepace::FramepacePlugin;

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
        .add_plugin(FrameTimeDiagnosticsPlugin)
        .add_plugin(LogDiagnosticsPlugin::default())
        .add_plugin(FramepacePlugin)
        .run();
}
