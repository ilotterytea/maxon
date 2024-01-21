use bevy::prelude::*;

use crate::{assets::AppAssets, constants::LIGHT_ROOM, startup_systems::CameraType};

use super::RoomState;

pub fn generate_game_scene(mut commands: Commands, app_assets: Res<AppAssets>) {
    commands.spawn(SceneBundle {
        scene: app_assets.mdl_maxon_room.clone(),
        ..default()
    });

    commands.spawn(PointLightBundle {
        point_light: PointLight {
            intensity: 5000.0,
            range: 15.0,
            shadows_enabled: true,
            color: LIGHT_ROOM,
            ..default()
        },
        transform: Transform::from_xyz(0.0, 7.2, 0.0),
        ..default()
    });
}

pub fn update_camera_transform(
    state: Res<State<RoomState>>,
    mut camera_query: Query<(&mut Transform, &CameraType), With<CameraType>>,
) {
    // maybe i could optimize it with state change detection
    // but not right now
    // so buy a $10k pc for this silly game
    if let Some((mut t, _)) = camera_query
        .iter_mut()
        .find(|x| x.1.eq(&CameraType::ThreeD))
    {
        let trs = state.get_camera_transform();
        let pos = trs.0;
        let rot = trs.1;

        t.translation = Vec3::from_array(pos);
        t.rotation = Quat::from_axis_angle(Vec3::new(rot[0], rot[1], rot[2]), rot[3]);
    }
}
