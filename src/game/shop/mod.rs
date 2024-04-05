use bevy::prelude::Resource;

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
