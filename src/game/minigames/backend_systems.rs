use std::f32::consts::PI;

use bevy::prelude::*;

use crate::systems::CameraComponent;

pub fn setup_minigame_backend(
    mut commands: Commands,
    camera_query: Query<Entity, With<CameraComponent>>,
) {
    if let Ok(e) = camera_query.get_single() {
        commands.entity(e).despawn_recursive();
        commands.spawn((Camera2dBundle::default(), CameraComponent));
    }
}

pub fn despawn_minigame_backend(
    mut commands: Commands,
    camera_query: Query<Entity, With<CameraComponent>>,
) {
    if let Ok(e) = camera_query.get_single() {
        commands.entity(e).despawn_recursive();
        commands.spawn((
            Camera3dBundle {
                transform: Transform::from_xyz(2.28, 4.4, 4.7)
                    .with_rotation(Quat::from_rotation_y(PI)),
                ..default()
            },
            CameraComponent,
        ));
    }
}
