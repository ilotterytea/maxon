use bevy::prelude::*;
use signin::SigninIntegrationPlugin;

use crate::AppState;

mod signin;
mod systems;
pub mod ui;

pub struct OnlinePlugin;

impl Plugin for OnlinePlugin {
    fn build(&self, app: &mut App) {
        app.add_plugins(SigninIntegrationPlugin).add_systems(
            Update,
            (ui::login, ui::setup_login_button, systems::setup_user_data)
                .run_if(in_state(AppState::Menu)),
        );
    }
}

#[derive(Resource)]
pub(super) struct UserData {
    pub username: String,
}
