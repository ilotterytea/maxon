use bevy::prelude::*;

use crate::{animation::update_animations, AppState};

use self::{
    building::update_existing_buildings,
    item::{check_item_for_purchase, initialize_items, purchase_item},
    player::*,
    ui::*,
};

mod building;
mod item;
mod player;
mod ui;

pub struct GamePlugin;

impl Plugin for GamePlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            Startup,
            (
                initialize_items,
                init_player_data,
                generate_multiplier_timer,
            ),
        )
        .add_systems(OnEnter(AppState::Game), (generate_player, generate_ui))
        .add_systems(
            Update,
            (
                click_on_player,
                update_ui,
                purchase_item,
                check_item_for_purchase,
                update_existing_buildings,
                update_animations,
                tick_multiplier_timer,
            )
                .run_if(in_state(AppState::Game)),
        );
    }
}
