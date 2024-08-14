use bevy::prelude::*;

use crate::AppState;

mod systems;
pub mod ui;

pub struct MenuPlugin;

impl Plugin for MenuPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            OnEnter(AppState::Menu),
            (
                ui::setup_ui,
                systems::setup_scene,
                systems::set_music_source,
            ),
        )
        .add_systems(
            Update,
            systems::rotate_camera.run_if(in_state(AppState::Menu)),
        )
        .add_systems(
            Update,
            ui::ui_interaction.run_if(
                in_state(AppState::Menu)
                    .or_else(in_state(AppState::Game))
                    .or_else(in_state(AppState::MinigamesLobby)),
            ),
        )
        .add_systems(OnExit(AppState::Menu), systems::despawn_menu_objects);
    }
}
