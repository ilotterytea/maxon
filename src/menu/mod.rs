use bevy::prelude::*;

use crate::{localization::init_localization, AppState};

pub struct MenuPlugin;

impl Plugin for MenuPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(AppState::Menu), (init_localization))
            .add_systems(Update, move_to_game_screen.run_if(in_state(AppState::Menu)));
    }
}

pub fn move_to_game_screen(mut state: ResMut<NextState<AppState>>) {
    state.set(AppState::Game);
}
