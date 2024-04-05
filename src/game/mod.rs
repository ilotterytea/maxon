use bevy::prelude::*;

use crate::{animation::update_animations, constants::CAMERA_TRANSFORMS, AppState};

use self::{
    basement::GameBasementPlugin,
    player::*,
    shop::{systems::set_availability_for_control_buttons, ui::generate_shop_ui, ShopSettings},
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
        app.insert_resource(ShopSettings::default())
            .add_state::<RoomState>()
            .add_systems(Startup, (init_player_data, generate_multiplier_timer))
            .add_plugins(GameBasementPlugin)
            .add_plugins(GameUnitPlugin)
            .add_systems(OnEnter(AppState::Game), set_default_room_state)
            .add_systems(
                OnEnter(RoomState::LivingRoom),
                (
                    generate_player,
                    generate_control_ui,
                    generate_savegame_ui,
                    generate_game_scene,
                    generate_shop_ui,
                ),
            )
            .add_systems(
                Update,
                (
                    update_ui,
                    update_animations,
                    tick_multiplier_timer,
                    set_availability_for_control_buttons,
                )
                    .run_if(in_state(RoomState::LivingRoom).and_then(in_state(AppState::Game))),
            )
            .add_systems(
                Update,
                (update_camera_transform, update_player_look).run_if(in_state(AppState::Game)),
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
    pub fn get_camera_transform(&self) -> ([f32; 3], [f32; 4]) {
        match self {
            Self::Basement => CAMERA_TRANSFORMS[1],
            _ => CAMERA_TRANSFORMS[0],
        }
    }
}

fn set_default_room_state(mut state: ResMut<NextState<RoomState>>) {
    state.set(RoomState::LivingRoom);
}
