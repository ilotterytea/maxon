use bevy::prelude::*;

use self::{
    behaviour::{spawn_units, update_unit_amount},
    ui::generate_shop_ui,
};

use super::RoomState;

mod behaviour;
mod ui;

pub struct GameUnitPlugin;

impl Plugin for GameUnitPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            OnEnter(RoomState::LivingRoom),
            (spawn_units, generate_shop_ui),
        )
        .add_systems(
            Update,
            (update_unit_amount).run_if(in_state(RoomState::LivingRoom)),
        );
    }
}
