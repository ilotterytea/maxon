use bevy::prelude::*;

use crate::AppState;

mod components;
mod systems;

pub struct GamePlugin;

impl Plugin for GamePlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(AppState::Game), systems::setup_scene)
            .add_systems(OnExit(AppState::Game), systems::despawn_game_objects);
    }
}
