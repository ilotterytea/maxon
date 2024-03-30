use bevy::prelude::*;

use self::behaviour::spawn_units;

use super::RoomState;

mod behaviour;

pub struct GameUnitPlugin;

impl Plugin for GameUnitPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(RoomState::LivingRoom), spawn_units);
    }
}
