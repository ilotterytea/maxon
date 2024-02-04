use bevy::prelude::*;

pub fn spawn_3d_camera(mut commands: Commands) {
    commands.spawn(Camera3dBundle::default());
}
