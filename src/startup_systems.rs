use bevy::prelude::*;

#[derive(Component, PartialEq, Eq)]
pub enum CameraType {
    TwoD,
    ThreeD,
}

pub fn spawn_2d_camera(mut commands: Commands) {
    commands.spawn((
        Camera2dBundle {
            camera: Camera {
                order: 0,
                ..default()
            },
            ..default()
        },
        CameraType::TwoD,
    ));
}

pub fn spawn_3d_camera(mut commands: Commands) {
    commands.spawn((
        Camera3dBundle {
            camera: Camera {
                order: 1,
                ..default()
            },
            ..default()
        },
        CameraType::ThreeD,
    ));
}
