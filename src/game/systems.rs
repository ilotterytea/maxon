use std::f32::consts::PI;

use bevy::prelude::*;

use crate::{assets::AppAssets, constants::ROOM_LIGHTS};

use super::RoomState;

pub fn generate_game_scene(mut commands: Commands, app_assets: Res<AppAssets>) {
    // Living room
    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_maxon_room.clone(),
            ..default()
        },
        Name::new("Living Room"),
    ));

    commands.spawn((
        PointLightBundle {
            point_light: PointLight {
                intensity: 5000.0,
                range: 15.0,
                shadows_enabled: true,
                color: ROOM_LIGHTS[0],
                ..default()
            },
            transform: Transform::from_xyz(0.0, 7.2, 0.0),
            ..default()
        },
        Name::new("Living Room Light"),
    ));
}

pub fn update_camera_transform(
    state: Res<State<RoomState>>,
    mut camera_query: Query<&mut Transform, With<Camera>>,
) {
    // maybe i could optimize it with state change detection
    // but not right now
    // so buy a $10k pc for this silly game
    for mut t in camera_query.iter_mut() {
        let trs = state.get_camera_transform();
        let pos = trs.0;
        let rot = trs.1;

        t.translation = Vec3::from_array(pos);
        t.rotation = Quat::from_rotation_y(rot * PI / 180.0 * 2.2);
    }
}
