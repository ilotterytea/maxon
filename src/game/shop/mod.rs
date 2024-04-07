use bevy::prelude::*;

use crate::AppState;

use self::systems::*;
use self::ui::*;

use super::RoomState;

pub mod systems;
pub mod ui;

#[derive(Component, Clone, PartialEq, Eq, Default)]
pub enum ShopMultiplier {
    #[default]
    X1,
    X10,
}

#[derive(Component, Clone, PartialEq, Eq, Default)]
pub enum ShopMode {
    #[default]
    Buy,
    Sell,
}

#[derive(Resource, Default)]
pub struct ShopSettings {
    pub multiplier: ShopMultiplier,
    pub mode: ShopMode,
}

pub struct GameShopPlugin;

impl Plugin for GameShopPlugin {
    fn build(&self, app: &mut App) {
        app.insert_resource(ShopSettings::default())
            .add_systems(OnEnter(RoomState::LivingRoom), generate_shop_ui)
            .add_systems(
                Update,
                (set_shop_mode, set_shop_multiplier)
                    .run_if(in_state(RoomState::LivingRoom).and_then(in_state(AppState::Game))),
            );
    }
}
