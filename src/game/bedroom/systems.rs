use std::f32::consts::PI;

use bevy::prelude::*;

use crate::assets::AppAssets;

#[derive(Component)]
pub struct GameBedroomFurnitureComponent;

pub fn generate_bedroom(mut commands: Commands, app_assets: Res<AppAssets>) {
    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_bed.clone(),
            transform: Transform::from_xyz(-4.4, 0.0, 4.4)
                .with_scale(Vec3::new(1.25, 1.5, 1.5))
                .with_rotation(Quat::from_rotation_y(90.0 * PI / 180.0)),
            ..default()
        },
        GameBedroomFurnitureComponent,
        Name::new("Bed"),
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
