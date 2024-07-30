use bevy::prelude::*;

use crate::AppState;

mod systems;
mod ui;

pub struct MenuPlugin;

impl Plugin for MenuPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            OnEnter(AppState::Menu),
            (ui::setup_ui, systems::setup_scene),
        )
        .add_systems(
            Update,
            (ui::ui_interaction, systems::rotate_camera).run_if(in_state(AppState::Menu)),
        )
        .add_systems(OnExit(AppState::Menu), systems::despawn_menu_objects);
    }
}
