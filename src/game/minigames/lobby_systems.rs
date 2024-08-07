use std::f32::consts::PI;

use bevy::prelude::*;
use bevy_mod_picking::prelude::*;

use crate::{
    game::components::GameObjectComponent, systems::CameraComponent, AppState, ModelAssets,
    SpriteAssets,
};

use super::MinigameState;

#[derive(Component)]
pub struct MinigamesTriggerComponent;

pub fn spawn_minigames_trigger(
    mut commands: Commands,
    mut meshes: ResMut<Assets<Mesh>>,
    mut materials: ResMut<Assets<StandardMaterial>>,
) {
    commands.spawn((
        PbrBundle {
            mesh: meshes.add(Cuboid::new(5.0, 2.5, 0.01)),
            material: materials.add(Color::srgba(0.0, 0.0, 0.0, 0.0)),
            transform: Transform::from_xyz(3.0, 3.7, 5.0),
            ..default()
        },
        On::<Pointer<Click>>::run(click_on_minigames_trigger),
        Name::new("Minigames trigger"),
        MinigamesTriggerComponent,
        GameObjectComponent,
    ));
}

pub fn click_on_minigames_trigger(mut next_state: ResMut<NextState<AppState>>) {
    next_state.set(AppState::MinigamesLobby);
}

#[derive(Component)]
pub struct MinigameLobbyObjectComponent;

pub fn despawn_minigame_lobby_objects(
    mut commands: Commands,
    query: Query<Entity, With<MinigameLobbyObjectComponent>>,
) {
    for e in query.iter() {
        commands.entity(e).despawn_recursive();
    }
}

pub fn setup_minigames_scene(
    mut commands: Commands,
    model_assets: Res<ModelAssets>,
    sprite_assets: Res<SpriteAssets>,
    mut camera_query: Query<&mut Transform, With<CameraComponent>>,
    mut meshes: ResMut<Assets<Mesh>>,
    mut materials: ResMut<Assets<StandardMaterial>>,
) {
    let mut camera_transform = camera_query.single_mut();
    *camera_transform =
        Transform::from_xyz(2.28, 4.4, 4.7).with_rotation(Quat::from_rotation_y(PI));

    commands.spawn((
        SceneBundle {
            scene: model_assets.living_room.clone(),
            ..default()
        },
        Name::new("Living room scene"),
        MinigameLobbyObjectComponent,
    ));

    commands.spawn((
        PbrBundle {
            mesh: meshes.add(Cuboid::new(2.15, 1.35, 0.01)),
            material: materials.add(StandardMaterial {
                base_color_texture: Some(sprite_assets.pc_background.clone()),
                ..default()
            }),
            transform: Transform::from_xyz(2.3, 4.25, 6.66),
            ..default()
        },
        Name::new("PC Background"),
        MinigameLobbyObjectComponent,
    ));

    commands.spawn((
        PointLightBundle {
            point_light: PointLight {
                color: bevy::color::palettes::css::LIGHT_CYAN.into(),
                intensity: 250000.0,
                radius: 200.0,
                range: 100.0,
                shadows_enabled: true,
                ..default()
            },
            transform: Transform::from_xyz(2.3, 4.2, 4.0),
            ..default()
        },
        Name::new("PC point light"),
        MinigameLobbyObjectComponent,
    ));

    commands.spawn((
        PbrBundle {
            mesh: meshes.add(Cuboid::new(0.3, 0.3, 0.01)),
            material: materials.add(StandardMaterial {
                base_color_texture: Some(sprite_assets.runner_icon.clone()),
                ..default()
            }),
            transform: Transform::from_xyz(3.1, 4.7, 6.65),
            ..default()
        },
        On::<Pointer<Click>>::run(play_runner),
        Name::new("Runner Icon"),
        MinigameLobbyObjectComponent,
    ));
}

fn play_runner(
    next_state: ResMut<NextState<AppState>>,
    next_mg_state: ResMut<NextState<MinigameState>>,
) {
    play_minigame(MinigameState::Runner, next_state, next_mg_state);
}

fn play_minigame(
    game: MinigameState,
    mut next_state: ResMut<NextState<AppState>>,
    mut next_mg_state: ResMut<NextState<MinigameState>>,
) {
    next_state.set(AppState::Minigame);
    next_mg_state.set(game);
}
