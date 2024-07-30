use bevy::{
    prelude::*,
    window::{PrimaryWindow, WindowMode},
};
use bevy_persistent::Persistent;

use crate::{localization::setup_localization, persistent::Settings, AppState};

pub struct BootPlugin;

impl Plugin for BootPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            OnEnter(AppState::Boot),
            (setup_localization, apply_settings),
        )
        .add_systems(Update, move_to_menu_screen.run_if(in_state(AppState::Boot)));
    }
}

fn move_to_menu_screen(mut state: ResMut<NextState<AppState>>) {
    state.set(AppState::Menu);
}

fn apply_settings(
    settings: Res<Persistent<Settings>>,
    mut window: Query<&mut Window, With<PrimaryWindow>>,
) {
    let mut window = window.single_mut();

    if settings.is_fullscreen {
        window.mode = WindowMode::BorderlessFullscreen;
    }
}
