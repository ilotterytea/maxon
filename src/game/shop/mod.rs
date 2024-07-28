use bevy::prelude::*;
use systems::PurchaseEvent;

use crate::AppState;

pub mod pets;
mod systems;
mod ui;

pub(super) struct ShopPlugin;

impl Plugin for ShopPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            Startup,
            (setup_shop_settings, systems::setup_multiplier_tick),
        )
        .add_systems(OnEnter(AppState::Game), ui::setup_ui)
        .add_systems(
            Update,
            (
                ui::listen_shop_control_changes,
                ui::update_player_stats,
                ui::toggle_pet_nodes,
                ui::pet_node_interaction,
                ui::update_pet_nodes,
                ui::update_pet_amount,
                systems::tick_multiplier,
                pets::pet_generation,
                pets::update_pet_position,
                pets::pet_revolution,
            )
                .chain()
                .run_if(in_state(AppState::Game)),
        )
        .add_event::<PurchaseEvent>();
    }
}

#[derive(Component, Clone, Copy, PartialEq, Eq, PartialOrd, Ord, Default)]
pub(super) enum ShopMode {
    #[default]
    Buy,
    Sell,
}

#[derive(Component, Clone, Copy, PartialEq, Eq, PartialOrd, Ord, Default)]
pub(super) enum ShopMultiplier {
    #[default]
    X1,
    X10,
}

impl ShopMultiplier {
    pub fn as_i32(&self) -> i32 {
        match self {
            Self::X1 => 1,
            Self::X10 => 10,
        }
    }
}

#[derive(Resource, Default)]
pub(super) struct ShopSettings {
    pub mode: ShopMode,
    pub multiplier: ShopMultiplier,
}

fn setup_shop_settings(mut commands: Commands) {
    commands.insert_resource(ShopSettings::default());
}
