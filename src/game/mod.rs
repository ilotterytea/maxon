use bevy::prelude::*;

use crate::AppState;

mod components;
mod player;
mod shop;
mod systems;

pub struct GamePlugin;

impl Plugin for GamePlugin {
    fn build(&self, app: &mut App) {
        app.add_plugins(shop::ShopPlugin)
            .add_systems(
                OnEnter(AppState::Game),
                (systems::setup_scene, player::setup_player),
            )
            .add_systems(
                Update,
                systems::sprites_looking_at_camera.run_if(in_state(AppState::Game)),
            )
            .add_systems(OnExit(AppState::Game), systems::despawn_game_objects);
    }
}
