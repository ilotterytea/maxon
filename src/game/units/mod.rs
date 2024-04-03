use bevy::prelude::*;

use self::behaviour::{spawn_units, update_unit_amount};

use super::RoomState;

mod behaviour;

pub struct GameUnitPlugin;

impl Plugin for GameUnitPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(RoomState::LivingRoom), spawn_units)
            .add_systems(
                Update,
                (update_unit_amount).run_if(in_state(RoomState::LivingRoom)),
            );
    }
}
