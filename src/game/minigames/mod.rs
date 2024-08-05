use bevy::prelude::*;

use crate::AppState;

mod systems;

pub struct MinigamesPlugin;

impl Plugin for MinigamesPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(AppState::Game), systems::spawn_minigames_trigger)
            .add_systems(
                OnEnter(AppState::MinigamesLobby),
                systems::setup_minigames_scene,
            );
    }
}
