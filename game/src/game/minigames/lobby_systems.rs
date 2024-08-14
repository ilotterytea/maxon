use std::f32::consts::PI;

use bevy::prelude::*;
use bevy_mod_picking::prelude::*;
use bevy_persistent::Persistent;

use crate::{
    game::components::GameObjectComponent, menu::ui::MenuControlComponent, persistent::Settings,
    systems::CameraComponent, AppState, GUIAssets, ModelAssets,
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
    mut camera_query: Query<&mut Transform, With<CameraComponent>>,
    mut meshes: ResMut<Assets<Mesh>>,
    mut materials: ResMut<Assets<StandardMaterial>>,
    gui_assets: Res<GUIAssets>,
    settings: Res<Persistent<Settings>>,
) {
    let mut camera_transform = camera_query.single_mut();

    *camera_transform =
        Transform::from_xyz(3.0, 6.6, 0.8).looking_at(Vec3::new(3.0, 4.0, 6.0), Vec3::Y);

    commands.spawn((
        SceneBundle {
            scene: model_assets.living_room.clone(),
            ..default()
        },
        Name::new("Living room scene"),
        MinigameLobbyObjectComponent,
    ));

    commands.spawn((
        SceneBundle {
            scene: model_assets.poker_table_prop.clone(),
            transform: Transform::from_xyz(3.0, 0.0, 6.0)
                .with_rotation(Quat::from_rotation_y(90.0 * PI / 180.0))
                .with_scale(Vec3::splat(3.5)),
            ..default()
        },
        Name::new("Poker table prop"),
        GameObjectComponent,
    ));

    commands.spawn((
        SceneBundle {
            scene: model_assets.slots_machine_prop.clone(),
            transform: Transform::from_xyz(4.5, 3.0, 6.0)
                .with_rotation(Quat::from_rotation_y(30.0 * PI / 180.0))
                .with_scale(Vec3::splat(1.5)),
            ..default()
        },
        Name::new("Slots machine prop"),
        GameObjectComponent,
    ));

    commands.spawn((
        PointLightBundle {
            point_light: PointLight {
                color: bevy::color::palettes::css::SALMON.into(),
                intensity: 250000.0,
                radius: 200.0,
                range: 100.0,
                shadows_enabled: true,
                ..default()
            },
            transform: Transform::from_xyz(3.8, 5.5, 5.1),
            ..default()
        },
        Name::new("Slots Point light"),
        MinigameLobbyObjectComponent,
    ));

    commands.spawn((
        PbrBundle {
            mesh: meshes.add(Cuboid::new(1.8, 1.2, 1.8)),
            material: materials.add(Color::srgba(0.0, 0.0, 0.0, 0.0)),
            transform: Transform::from_xyz(4.5, 3.9, 5.9)
                .with_rotation(Quat::from_rotation_y(30.0 * PI / 180.0)),
            ..default()
        },
        On::<Pointer<Click>>::run(play_slots),
        Name::new("Slots Hitbox"),
        MinigameLobbyObjectComponent,
    ));

    commands
        .spawn((
            NodeBundle {
                style: Style {
                    padding: UiRect::all(Val::Px(5.0)),
                    column_gap: Val::Px(5.0),
                    ..default()
                },
                ..default()
            },
            MinigameLobbyObjectComponent,
            Name::new("UI"),
        ))
        .with_children(|root| {
            // Exit button
            root.spawn((
                ButtonBundle {
                    image: UiImage::new(gui_assets.exit.clone()),
                    style: Style {
                        width: Val::Px(57.0),
                        height: Val::Px(64.0),
                        ..default()
                    },
                    ..default()
                },
                Name::new("Exit button"),
                MenuControlComponent::MinigameLobbyBack,
            ));

            // Music button
            root.spawn((
                ButtonBundle {
                    image: UiImage::new(if settings.music {
                        gui_assets.music_on.clone()
                    } else {
                        gui_assets.music_off.clone()
                    }),
                    style: Style {
                        width: Val::Px(79.0),
                        height: Val::Px(64.0),
                        ..default()
                    },
                    ..default()
                },
                Name::new("Music button"),
                MenuControlComponent::Music,
            ));
        });
}

fn play_slots(
    next_state: ResMut<NextState<AppState>>,
    next_mg_state: ResMut<NextState<MinigameState>>,
) {
    play_minigame(MinigameState::Slots, next_state, next_mg_state);
}

fn play_minigame(
    game: MinigameState,
    mut next_state: ResMut<NextState<AppState>>,
    mut next_mg_state: ResMut<NextState<MinigameState>>,
) {
    next_state.set(AppState::Minigame);
    next_mg_state.set(game);
}
