use bevy::prelude::*;
use game::GamePlugin;

mod game;

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

    #[cfg(feature = "debug")]
    {
        // Diagnostics
        app.add_plugins((
            bevy::diagnostic::FrameTimeDiagnosticsPlugin,
            bevy::diagnostic::LogDiagnosticsPlugin::default(),
        ))
    }

    app.run();
}

#[derive(Clone, PartialEq, Eq, Hash, Debug, Default, States)]
pub enum AppState {
    #[default]
    Boot,
    Game,
}
