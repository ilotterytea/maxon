use bevy::prelude::*;

use crate::AppState;

use self::{
    building::{
        generate_buildings, update_building_position, update_building_units,
        update_selected_building_index,
    },
    ui::building_movement_buttons,
};

use super::RoomState;

pub mod building;
mod ui;

pub(super) struct GameBasementPlugin;

impl Plugin for GameBasementPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            OnEnter(RoomState::Basement),
            (generate_buildings, building_movement_buttons),
        )
        .add_systems(
            Update,
            (
                update_building_position,
                update_selected_building_index,
                update_building_units,
            )
                .run_if(in_state(RoomState::Basement).and_then(in_state(AppState::Game))),
        );
    }
}
