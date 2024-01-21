use bevy::prelude::*;

use crate::{animation::update_animations, constants::CAMERA_TRANSFORMS, AppState};

use self::{
    building::update_existing_buildings,
    item::{check_item_for_purchase, initialize_items, purchase_item},
    player::*,
    systems::{generate_game_scene, update_camera_transform},
    ui::*,
};

mod building;
mod item;
mod player;
mod systems;
mod ui;

pub struct GamePlugin;

impl Plugin for GamePlugin {
    fn build(&self, app: &mut App) {
        app.add_state::<RoomState>()
            .add_systems(
                Startup,
                (
                    initialize_items,
                    init_player_data,
                    generate_multiplier_timer,
                ),
            )
            .add_systems(
                OnEnter(AppState::Game),
                (generate_player, generate_control_ui, generate_game_scene),
            )
            .add_systems(
                Update,
                (
                    update_ui,
                    purchase_item,
                    check_item_for_purchase,
                    update_existing_buildings,
                    update_animations,
                    tick_multiplier_timer,
                    update_camera_transform,
                )
                    .run_if(in_state(RoomState::LivingRoom).and_then(in_state(AppState::Menu))),
            );
    }
}

#[derive(Clone, Eq, PartialEq, Debug, Hash, Default, States)]
pub enum RoomState {
    #[default]
    LivingRoom,
    Kitchen,
    Bedroom,
    Basement,
}

impl RoomState {
    pub fn get_camera_transform(&self) -> ([f32; 3], [f32; 4]) {
        match self {
            _ => CAMERA_TRANSFORMS[0],
        }
    }
}
