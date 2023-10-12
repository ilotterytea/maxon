use bevy::prelude::*;

use crate::AppState;

use self::{player::*, ui::*};

mod player;
mod ui;

pub struct GamePlugin;

impl Plugin for GamePlugin {
    fn build(&self, app: &mut App) {
        app.insert_resource(PlayerData::default())
            .add_systems(OnEnter(AppState::Game), (generate_player, generate_ui))
            .add_systems(
                Update,
                (click_on_player, update_ui).run_if(in_state(AppState::Game)),
            );
    }
}
