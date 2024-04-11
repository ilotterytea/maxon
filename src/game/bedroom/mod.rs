use bevy::prelude::*;

use self::systems::*;

use super::RoomState;

mod systems;

pub struct GameBedroomPlugin;

impl Plugin for GameBedroomPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(RoomState::Bedroom), generate_bedroom)
            .add_systems(OnExit(RoomState::Bedroom), despawn_bedroom);
    }
}
