use std::f32::consts::PI;

use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::{
    boot::MusicSourceComponent, persistent::Settings, systems::CameraComponent, ModelAssets,
    MusicAssets,
};

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

    #[cfg(any(target_os = "android", target_os = "ios"))]
    commands.spawn((
        SceneBundle {
            scene: model_assets.battlestation_prop.clone(),
            transform: Transform::from_xyz(3.0, 0.0, 6.0),
            ..default()
        },
        Name::new("Battlestation prop"),
        MenuObjectComponent,
    ));

    #[cfg(not(any(target_os = "android", target_os = "ios")))]
    commands.spawn((
        SceneBundle {
            scene: model_assets.poker_table_prop.clone(),
            transform: Transform::from_xyz(3.0, 0.0, 6.0)
                .with_rotation(Quat::from_rotation_y(90.0 * PI / 180.0))
                .with_scale(Vec3::splat(3.5)),
            ..default()
        },
        Name::new("Poker table prop"),
        MenuObjectComponent,
    ));

    #[cfg(not(any(target_os = "android", target_os = "ios")))]
    commands.spawn((
        SceneBundle {
            scene: model_assets.slots_machine_prop.clone(),
            transform: Transform::from_xyz(4.5, 3.0, 6.0)
                .with_rotation(Quat::from_rotation_y(30.0 * PI / 180.0))
                .with_scale(Vec3::splat(1.5)),
            ..default()
        },
        Name::new("Slots machine prop"),
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

pub fn set_music_source(
    mut commands: Commands,
    sink_query: Query<Entity, (With<MusicSourceComponent>, With<AudioSink>)>,
    music_assets: Res<MusicAssets>,
    settings: Res<Persistent<Settings>>,
) {
    for e in sink_query.iter() {
        let source = &music_assets.menu;

        commands.entity(e).remove::<AudioSink>();
        commands.entity(e).insert(AudioBundle {
            source: source.clone(),
            settings: PlaybackSettings {
                mode: bevy::audio::PlaybackMode::Loop,
                paused: !settings.music,
                ..default()
            },
        });
    }
}
