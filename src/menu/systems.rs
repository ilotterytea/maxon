use std::f32::consts::PI;

use bevy::prelude::*;

use crate::{systems::CameraComponent, ModelAssets};

#[derive(Component)]
pub struct MenuObjectComponent;

pub fn despawn_menu_objects(
    mut commands: Commands,
    query: Query<Entity, With<MenuObjectComponent>>,
) {
    for e in query.iter() {
        commands.entity(e).despawn_recursive();
    }
}

pub fn setup_scene(
    mut commands: Commands,
    mut camera_query: Query<&mut Transform, With<CameraComponent>>,
    model_assets: Res<ModelAssets>,
) {
    let mut camera_transform = camera_query.single_mut();
    *camera_transform = Transform::from_xyz(0.0, 5.0, 0.0);

    commands.spawn((
        SceneBundle {
            scene: model_assets.living_room.clone(),
            ..default()
        },
        Name::new("Living room scene"),
        MenuObjectComponent,
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
        MenuObjectComponent,
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
            transform: Transform::from_xyz(0.0, 4.0, 1.2),
            ..default()
        },
        Name::new("Main point light"),
        MenuObjectComponent,
    ));
}

pub fn rotate_camera(
    time: Res<Time>,
    mut camera_query: Query<&mut Transform, With<CameraComponent>>,
) {
    let mut camera_transform = camera_query.single_mut();

    camera_transform.rotation =
        Quat::from_rotation_y(time.delta_seconds() * PI / 64.0) * camera_transform.rotation;
}
