use std::f32::consts::PI;

use bevy::prelude::*;

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
        SpotLightBundle {
            spot_light: SpotLight {
                color: Color::WHITE,
                intensity: 1000000.0,
                range: 2000.0,
                radius: 100.0,
                shadows_enabled: true,
                inner_angle: PI / 4.0 * 1.2,
                outer_angle: PI / 4.0 * 1.8,
                ..default()
            },
            transform: Transform::from_xyz(0.0, 12.0, 0.0)
                .with_rotation(Quat::from_rotation_x(-90.0 * PI / 180.0)),
            ..default()
        },
        Name::new("Main spotlight"),
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
