use bevy::prelude::*;

use crate::{assets::AppAssets, constants::LIGHT_ROOM, startup_systems::CameraType};

pub fn generate_game_scene(
    mut commands: Commands,
    app_assets: Res<AppAssets>,
    mut camera_query: Query<(&mut Transform, &CameraType), With<CameraType>>,
) {
    commands.spawn(SceneBundle {
        scene: app_assets.mdl_maxon_room.clone(),
        ..default()
    });

    commands.spawn(PointLightBundle {
        point_light: PointLight {
            intensity: 4000.0,
            shadows_enabled: true,
            color: LIGHT_ROOM,
            ..default()
        },
        transform: Transform::from_xyz(0.0, 7.0, 0.0),
        ..default()
    });

    if let Some((mut t, _)) = camera_query
        .iter_mut()
        .find(|x| x.1.eq(&CameraType::ThreeD))
    {
        t.translation = Vec3::new(2.0, 5.0, 2.0);
        t.rotation = Quat::from_xyzw(-0.06, 0.4, 0.03, 0.91);
    }
}
