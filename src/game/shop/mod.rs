use bevy::prelude::*;

use crate::AppState;

mod ui;

pub struct ShopPlugin;

impl Plugin for ShopPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(Startup, setup_shop_settings)
            .add_systems(OnEnter(AppState::Game), ui::setup_ui);
    }
}

#[derive(Component, Default)]
pub enum ShopMode {
    #[default]
    Buy,
    Sell,
}

#[derive(Component, Default)]
pub enum ShopMultiplier {
    #[default]
    X1,
    X10,
}

#[derive(Resource, Default)]
pub struct ShopSettings {
    pub mode: ShopMode,
    pub multiplier: ShopMultiplier,
}

fn setup_shop_settings(mut commands: Commands) {
    commands.insert_resource(ShopSettings::default());
}
