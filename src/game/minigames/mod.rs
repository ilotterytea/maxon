use bevy::prelude::*;

use crate::AppState;

mod backend_systems;
mod lobby_systems;

pub struct MinigamesPlugin;

impl Plugin for MinigamesPlugin {
    fn build(&self, app: &mut App) {
        app.init_state::<MinigameState>()
            .add_systems(
                OnEnter(AppState::Game),
                lobby_systems::spawn_minigames_trigger,
            )
            .add_systems(
                OnEnter(AppState::MinigamesLobby),
                lobby_systems::setup_minigames_scene,
            )
            .add_systems(
                OnExit(AppState::MinigamesLobby),
                lobby_systems::despawn_minigame_lobby_objects,
            )
            // Minigame backend setup
            .add_systems(
                OnEnter(MinigameState::None),
                backend_systems::despawn_minigame_backend,
            )
            .add_systems(
                OnExit(MinigameState::None),
                backend_systems::setup_minigame_backend,
            );
    }
}

#[derive(Clone, PartialEq, Eq, Hash, Debug, Default, States)]
pub enum MinigameState {
    #[default]
    None,
}
