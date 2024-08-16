use bevy::prelude::*;
use signin::SigninIntegrationPlugin;

mod signin;

pub struct OnlinePlugin;

impl Plugin for OnlinePlugin {
    fn build(&self, app: &mut App) {
        app.add_plugins(SigninIntegrationPlugin);
    }
}
