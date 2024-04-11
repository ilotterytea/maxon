use std::f32::consts::PI;

use bevy::prelude::*;
use bevy_mod_picking::{
    events::{Click, Pointer},
    prelude::On,
};

use crate::{assets::AppAssets, game::PlayerComponent};

#[derive(Component)]
pub struct GameBedroomFurnitureComponent;

#[derive(Component)]
pub enum GameBedroomLightComponent {
    Primary,
    Secondary,
}

pub(super) fn generate_bedroom(mut commands: Commands, app_assets: Res<AppAssets>) {
    let rot = Quat::from_rotation_y(90.0 * PI / 180.0);

    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_bed.clone(),
            transform: Transform::from_xyz(-4.4, 0.0, 4.4)
                .with_scale(Vec3::new(1.25, 1.5, 1.5))
                .with_rotation(rot.clone()),
            ..default()
        },
        GameBedroomFurnitureComponent,
        Name::new("Bed"),
    ));

    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_shelf.clone(),
            transform: Transform::from_xyz(-9.5, 4.5, 5.1)
                .with_scale(Vec3::new(0.8, 0.8, 0.8))
                .with_rotation(rot.clone()),
            ..default()
        },
        GameBedroomFurnitureComponent,
        Name::new("Shelf"),
    ));

    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_bedside_table.clone(),
            transform: Transform::from_xyz(-8.8, 0.8, 8.0)
                .with_scale(Vec3::new(0.8, 0.8, 0.8))
                .with_rotation(rot.clone()),
            ..default()
        },
        GameBedroomFurnitureComponent,
        Name::new("Bedside table"),
    ));

    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_lamp.clone(),
            transform: Transform::from_xyz(-8.8, 2.6, 8.0),
            ..default()
        },
        GameBedroomFurnitureComponent,
        On::<Pointer<Click>>::run(click_on_lamp),
        Name::new("Lamp"),
    ));

    commands.spawn((
        PointLightBundle {
            point_light: PointLight {
                color: Color::ORANGE,
                intensity: 1000.0,
                range: 8.0,
                shadows_enabled: true,
                ..default()
            },
            transform: Transform::from_xyz(-6.6, 4.0, 6.6),
            ..default()
        },
        GameBedroomFurnitureComponent,
        GameBedroomLightComponent::Primary,
        Name::new("Bedroom Light #1"),
    ));

    commands.spawn((
        PointLightBundle {
            point_light: PointLight {
                color: Color::ORANGE,
                intensity: 200.0,
                range: 8.0,
                shadows_enabled: false,
                ..default()
            },
            transform: Transform::from_xyz(-3.7, 4.0, 3.0),
            ..default()
        },
        GameBedroomFurnitureComponent,
        GameBedroomLightComponent::Secondary,
        Name::new("Bedroom Light #2"),
    ));
}

pub(super) fn despawn_bedroom(
    mut commands: Commands,
    query: Query<Entity, With<GameBedroomFurnitureComponent>>,
) {
    for e in query.iter() {
        commands.entity(e).despawn_recursive();
    }
}

pub(super) fn click_on_lamp(mut player_query: Query<&mut PlayerComponent, With<PlayerComponent>>) {
    for mut p in player_query.iter_mut() {
        p.is_sleeping = !p.is_sleeping;
    }
}

pub(super) fn kill_the_lights(
    player_query: Query<&PlayerComponent, (With<PlayerComponent>, Changed<PlayerComponent>)>,
    mut query: Query<
        (&mut PointLight, &GameBedroomLightComponent),
        With<GameBedroomLightComponent>,
    >,
) {
    for player in player_query.iter() {
        let (i, i2) = if player.is_sleeping {
            (0.0, 0.0)
        } else {
            (1000.0, 200.0)
        };

        for (mut p, b) in query.iter_mut() {
            p.intensity = match *b {
                GameBedroomLightComponent::Primary => i,
                GameBedroomLightComponent::Secondary => i2,
            };
        }
    }
}
