use bevy::prelude::*;

use crate::{
    animation::update_animations,
    constants::{CAMERA_TRANSFORMS, PLAYER_POSITIONS, PLAYER_SCALES},
    AppState,
};

use self::{
    basement::GameBasementPlugin, bedroom::GameBedroomPlugin, player::*, shop::GameShopPlugin,
    systems::*, ui::*, units::GameUnitPlugin,
};

pub mod basement;
mod bedroom;
mod player;
mod shop;
mod systems;
mod ui;
mod units;

pub struct GamePlugin;

impl Plugin for GamePlugin {
    fn build(&self, app: &mut App) {
        app.add_state::<RoomState>()
            .add_systems(Startup, (init_player_data, generate_multiplier_timer))
            .add_plugins(GameBasementPlugin)
            .add_plugins(GameUnitPlugin)
            .add_plugins(GameShopPlugin)
            .add_plugins(GameBedroomPlugin)
            .add_systems(
                OnEnter(AppState::Game),
                (
                    set_default_room_state,
                    generate_game_scene,
                    generate_control_ui,
                    generate_savegame_ui,
                    generate_player,
                ),
            )
            .add_systems(
                Update,
                (
                    update_ui,
                    update_animations,
                    tick_multiplier_timer,
                    update_camera_transform,
                    update_player_look,
                    handle_control_buttons,
                    update_player_position_and_scale,
                    update_light_intensity,
                    control_player_needs,
                )
                    .run_if(in_state(AppState::Game)),
            )
            .add_systems(
                OnExit(AppState::Game),
                (despawn_control_ui, despawn_savegame_ui, despawn_player),
            );
    }
}

#[derive(Clone, Eq, PartialEq, Debug, Hash, Default, States)]
pub enum RoomState {
    LivingRoom,
    Kitchen,
    Bedroom,
    Basement,
    #[default]
    None,
}

impl RoomState {
    fn get_index(&self) -> usize {
        match self {
            Self::Bedroom => 1,
            _ => 0,
        }
    }

    pub fn get_camera_transform(&self) -> ([f32; 3], f32) {
        CAMERA_TRANSFORMS[self.get_index()]
    }

    pub fn get_player_position(&self) -> [f32; 3] {
        PLAYER_POSITIONS[self.get_index()]
    }

    pub fn get_player_scale(&self) -> [f32; 3] {
        PLAYER_SCALES[self.get_index()]
    }
}

fn set_default_room_state(mut state: ResMut<NextState<RoomState>>) {
    state.set(RoomState::LivingRoom);
}
