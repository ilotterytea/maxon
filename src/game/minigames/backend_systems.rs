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
                transform: Transform::from_xyz(3.0, 6.6, 0.8)
                    .looking_at(Vec3::new(3.0, 4.0, 6.0), Vec3::Y),
                ..default()
            },
            CameraComponent,
        ));
    }
}
