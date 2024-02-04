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

    // Basement
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
        t.rotation = Quat::from_axis_angle(Vec3::new(rot[0], rot[1], rot[2]), rot[3]);
    }
}
