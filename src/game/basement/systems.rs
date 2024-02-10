use bevy::prelude::*;

use crate::{assets::AppAssets, constants::ROOM_LIGHTS};

pub fn generate_basement_scene(mut commands: Commands, app_assets: Res<AppAssets>) {
    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_basement_room.clone(),
            transform: Transform::from_xyz(0.0, -13.0, 0.0),
            ..default()
        },
        Name::new("Basement Room"),
    ));

    commands.spawn((
        PointLightBundle {
            point_light: PointLight {
                intensity: 4000.0,
                range: 48.0,
                shadows_enabled: true,
                color: ROOM_LIGHTS[1],
                ..default()
            },
            transform: Transform::from_xyz(0.0, -8.0, 0.0),
            ..default()
        },
        Name::new("Basement Room Light"),
    ));
}
