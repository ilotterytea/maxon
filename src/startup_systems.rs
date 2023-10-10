use bevy::prelude::*;

pub fn spawn_2d_camera(mut commands: Commands) {
    commands.spawn(Camera2dBundle::default());
}
