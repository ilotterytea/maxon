use bevy::prelude::*;

use crate::AppState;

mod components;
mod gift;
mod player;
pub mod shop;
mod systems;
mod ui;

pub struct GamePlugin;

impl Plugin for GamePlugin {
    fn build(&self, app: &mut App) {
        app.add_plugins(shop::ShopPlugin)
            .add_systems(
                OnEnter(AppState::Game),
                (
                    systems::setup_scene,
                    player::setup_player,
                    player::setup_play_timestamp,
                    ui::setup_ui,
                    systems::set_music_source,
                    gift::setup_gift_box,
                ),
            )
            .add_systems(
                Update,
                (
                    systems::sprites_looking_at_camera,
                    systems::update_music_source,
                    gift::update_gift_box,
                    gift::spin_rays,
                    gift::update_giftbox_notification_position,
                )
                    .run_if(in_state(AppState::Game)),
            )
            .add_systems(
                OnExit(AppState::Game),
                (systems::despawn_game_objects, player::set_played_time),
            );
    }
}
