use bevy::prelude::*;

use self::systems::*;
use self::ui::*;

use super::RoomState;

pub mod systems;
pub mod ui;

#[derive(Default)]
pub enum ShopMultiplier {
    #[default]
    X1,
    X10,
}

#[derive(Default)]
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
            .add_systems(OnEnter(RoomState::LivingRoom), generate_shop_ui);
    }
}
