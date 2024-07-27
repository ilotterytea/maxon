use bevy::prelude::*;

use crate::{localization::setup_localization, AppState};

pub struct BootPlugin;

impl Plugin for BootPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(AppState::Boot), setup_localization)
            .add_systems(Update, move_to_game_screen.run_if(in_state(AppState::Boot)));
    }
}

fn move_to_game_screen(mut state: ResMut<NextState<AppState>>) {
    state.set(AppState::Game);
}
