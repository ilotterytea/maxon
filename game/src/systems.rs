use bevy::prelude::*;

#[derive(Component)]
pub struct CameraComponent;

pub fn setup_camera(mut commands: Commands) {
    commands.spawn((Camera3dBundle::default(), CameraComponent));
}
