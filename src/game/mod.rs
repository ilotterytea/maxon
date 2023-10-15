use bevy::prelude::*;

use crate::AppState;

use self::{
    item::{check_item_for_purchase, initialize_items, purchase_item},
    player::*,
    ui::*,
};

mod item;
mod player;
mod ui;

pub struct GamePlugin;

impl Plugin for GamePlugin {
    fn build(&self, app: &mut App) {
        app.insert_resource(PlayerData::default())
            .add_systems(Startup, initialize_items)
            .add_systems(OnEnter(AppState::Game), (generate_player, generate_ui))
            .add_systems(
                Update,
                (
                    click_on_player,
                    update_ui,
                    purchase_item,
                    check_item_for_purchase,
                )
                    .run_if(in_state(AppState::Game)),
            );
    }
}
