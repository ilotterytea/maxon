use bevy::prelude::*;

use crate::AppState;

use self::player::*;

mod player;

pub struct GamePlugin;

impl Plugin for GamePlugin {
    fn build(&self, app: &mut App) {
        app.insert_resource(PlayerData::default())
            .add_systems(OnEnter(AppState::Game), generate_player)
            .add_systems(Update, (click_on_player).run_if(in_state(AppState::Game)));
    }
}
