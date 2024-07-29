use bevy::prelude::*;

use crate::AppState;

mod systems;
mod ui;

pub struct MenuPlugin;

impl Plugin for MenuPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(AppState::Menu), ui::setup_ui)
            .add_systems(Update, ui::ui_interaction.run_if(in_state(AppState::Menu)))
            .add_systems(OnExit(AppState::Menu), systems::despawn_menu_objects);
    }
}
