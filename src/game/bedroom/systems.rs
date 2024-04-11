use std::f32::consts::PI;

use bevy::prelude::*;

use crate::assets::AppAssets;

#[derive(Component)]
pub struct GameBedroomFurnitureComponent;

pub fn generate_bedroom(mut commands: Commands, app_assets: Res<AppAssets>) {
    let rot = Quat::from_rotation_y(90.0 * PI / 180.0);

    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_bed.clone(),
            transform: Transform::from_xyz(-4.4, 0.0, 4.4)
                .with_scale(Vec3::new(1.25, 1.5, 1.5))
                .with_rotation(rot.clone()),
            ..default()
        },
        GameBedroomFurnitureComponent,
        Name::new("Bed"),
    ));

    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_shelf.clone(),
            transform: Transform::from_xyz(-9.5, 4.5, 5.1)
                .with_scale(Vec3::new(0.8, 0.8, 0.8))
                .with_rotation(rot.clone()),
            ..default()
        },
        GameBedroomFurnitureComponent,
        Name::new("Shelf"),
    ));

    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_bedside_table.clone(),
            transform: Transform::from_xyz(-8.8, 0.8, 8.0)
                .with_scale(Vec3::new(0.8, 0.8, 0.8))
                .with_rotation(rot.clone()),
            ..default()
        },
        GameBedroomFurnitureComponent,
        Name::new("Bedside table"),
    ));

    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_lamp.clone(),
            transform: Transform::from_xyz(-8.8, 2.6, 8.0),
            ..default()
        },
        GameBedroomFurnitureComponent,
        Name::new("Lamp"),
    ));
}

pub fn despawn_bedroom(
    mut commands: Commands,
    query: Query<Entity, With<GameBedroomFurnitureComponent>>,
) {
    for e in query.iter() {
        commands.entity(e).despawn_recursive();
    }
}
