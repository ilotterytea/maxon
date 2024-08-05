use std::f32::consts::PI;

use bevy::prelude::*;
use bevy_mod_picking::prelude::*;

use crate::{
    game::components::GameObjectComponent, systems::CameraComponent, AppState, ModelAssets,
};

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

pub fn setup_minigames_scene(
    mut commands: Commands,
    model_assets: Res<ModelAssets>,
    mut camera_query: Query<&mut Transform, With<CameraComponent>>,
) {
    let mut camera_transform = camera_query.single_mut();
    *camera_transform = Transform::from_xyz(2.4, 4.4, 4.7).with_rotation(Quat::from_rotation_y(PI));

    commands.spawn((
        SceneBundle {
            scene: model_assets.living_room.clone(),
            ..default()
        },
        Name::new("Living room scene"),
        MinigameLobbyObjectComponent,
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
        MinigameLobbyObjectComponent,
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
            transform: Transform::from_xyz(0.0, 4.0, 1.2)
                .with_rotation(Quat::from_rotation_x(-90.0 * PI / 180.0)),
            ..default()
        },
        Name::new("Main point light"),
        MinigameLobbyObjectComponent,
    ));
}
