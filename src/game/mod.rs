use bevy::prelude::*;

use crate::{animation::update_animations, constants::CAMERA_TRANSFORMS, AppState};

use self::{
    basement::GameBasementPlugin,
    player::*,
    shop::GameShopPlugin,
    systems::{generate_game_scene, update_camera_transform},
    ui::*,
    units::GameUnitPlugin,
};

pub mod basement;
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
            .add_systems(
                OnEnter(AppState::Game),
                (
                    set_default_room_state,
                    generate_game_scene,
                    generate_control_ui,
                    generate_savegame_ui,
                ),
            )
            .add_systems(OnEnter(RoomState::LivingRoom), generate_player)
            .add_systems(
                Update,
                (
                    update_ui,
                    update_animations,
                    tick_multiplier_timer,
                    update_camera_transform,
                    update_player_look,
                )
                    .run_if(in_state(AppState::Game)),
            )
            .add_systems(OnExit(RoomState::LivingRoom), despawn_player)
            .add_systems(
                OnExit(AppState::Game),
                (despawn_control_ui, despawn_savegame_ui),
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
    pub fn get_camera_transform(&self) -> ([f32; 3], f32) {
        match self {
            Self::Basement => CAMERA_TRANSFORMS[1],
            _ => CAMERA_TRANSFORMS[0],
        }
    }
}

fn set_default_room_state(mut state: ResMut<NextState<RoomState>>) {
    state.set(RoomState::LivingRoom);
}
