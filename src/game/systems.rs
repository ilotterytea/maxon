use std::f32::consts::PI;

use bevy::prelude::*;
use bevy_sprite3d::Sprite3dComponent;

use crate::{assets::ModelAssets, systems::CameraComponent};

use super::components::GameObjectComponent;

pub fn setup_scene(
    mut commands: Commands,
    mut camera_query: Query<&mut Transform, With<CameraComponent>>,
    model_assets: Res<ModelAssets>,
) {
    let mut camera_transform = camera_query.single_mut();
    *camera_transform = Transform::from_xyz(-2.8, 1.7, -0.7)
        .with_rotation(Quat::from_rotation_y(245.0 * PI / 180.0));

    commands.spawn((
        SceneBundle {
            scene: model_assets.living_room.clone(),
            ..default()
        },
        Name::new("Living room scene"),
        GameObjectComponent,
    ));

    commands.spawn((
        PointLightBundle {
            point_light: PointLight {
                color: bevy::color::palettes::css::HOT_PINK.into(),
                intensity: 200000.0,
                radius: 200.0,
                range: 100.0,
                shadows_enabled: true,
                ..default()
            },
            transform: Transform::from_xyz(5.0, 6.4, 1.5),
            ..default()
        },
        Name::new("Offline point light"),
        GameObjectComponent,
    ));

    commands.spawn((
        PointLightBundle {
            point_light: PointLight {
                color: bevy::color::palettes::css::WHEAT.into(),
                intensity: 350000.0,
                radius: 200.0,
                range: 100.0,
                shadows_enabled: true,
                ..default()
            },
            transform: Transform::from_xyz(0.0, 4.0, 1.2)
                .with_rotation(Quat::from_rotation_x(-90.0 * PI / 180.0)),
            ..default()
        },
        Name::new("Main point light"),
        GameObjectComponent,
    ));
}

pub fn despawn_game_objects(
    mut commands: Commands,
    objects: Query<Entity, With<GameObjectComponent>>,
) {
    for o in objects.iter() {
        commands.entity(o).despawn_recursive();
    }
}

pub fn sprites_looking_at_camera(
    mut query: Query<&mut Transform, (With<Sprite3dComponent>, Without<CameraComponent>)>,
    camera_query: Query<
        &Transform,
        (
            With<CameraComponent>,
            Changed<Transform>,
            Without<Sprite3dComponent>,
        ),
    >,
) {
    if let Ok(camera_transform) = camera_query.get_single() {
        for mut t in query.iter_mut() {
            t.look_at(camera_transform.translation, Vec3::Y);
        }
    }
}
