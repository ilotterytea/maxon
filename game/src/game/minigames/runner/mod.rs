use bevy::prelude::*;

use super::MinigameState;

pub mod systems;

pub(super) struct RunnerMinigamePlugin;

impl Plugin for RunnerMinigamePlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(MinigameState::Runner), systems::setup_runner)
            .add_systems(
                Update,
                (
                    systems::jump_player,
                    systems::gravity_system,
                    systems::spawn_obstacles,
                    systems::update_obstacles,
                    systems::check_obstacle_collision,
                    systems::update_score,
                    systems::listen_keyboard_events,
                )
                    .run_if(in_state(MinigameState::Runner)),
            )
            .add_systems(OnExit(MinigameState::Runner), systems::despawn_runner);
    }
}
