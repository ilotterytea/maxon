use bevy::prelude::*;

use crate::AppState;

mod ui;

pub struct ShopPlugin;

impl Plugin for ShopPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(AppState::Game), ui::setup_ui);
    }
}
