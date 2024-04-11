use bevy::prelude::*;

use self::behaviour::*;

use super::RoomState;

mod behaviour;

pub struct GameUnitPlugin;

impl Plugin for GameUnitPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(RoomState::LivingRoom), spawn_units)
            .add_systems(
                Update,
                (update_unit_amount, update_unit_look).run_if(in_state(RoomState::LivingRoom)),
            )
            .add_systems(OnExit(RoomState::LivingRoom), despawn_units);
    }
}
