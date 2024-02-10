use bevy::prelude::*;

use crate::assets::AppAssets;

#[derive(Component, PartialEq, Eq, PartialOrd, Ord)]
pub(super) enum BuildingMovementButton {
    Left,
    Right,
}

pub(super) fn building_movement_buttons(mut commands: Commands, app_assets: Res<AppAssets>) {
    commands
        .spawn((
            ButtonBundle {
                style: Style {
                    position_type: PositionType::Absolute,
                    bottom: Val::Percent(0.0),
                    left: Val::Percent(0.0),
                    height: Val::Percent(100.0),
                    width: Val::Percent(15.0),

                    ..default()
                },
                background_color: Color::NONE.into(),
                ..default()
            },
            BuildingMovementButton::Left,
        ))
        .with_children(|parent| {
            parent.spawn(AtlasImageBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    height: Val::Percent(100.0),
                    ..default()
                },
                texture_atlas: app_assets.ui_gradient.clone(),
                texture_atlas_image: UiTextureAtlasImage {
                    index: 3,
                    ..default()
                },
                background_color: Color::BLACK.into(),
                ..default()
            });
        });

    commands
        .spawn((
            ButtonBundle {
                style: Style {
                    position_type: PositionType::Absolute,
                    bottom: Val::Percent(0.0),
                    right: Val::Percent(0.0),
                    height: Val::Percent(100.0),
                    width: Val::Percent(15.0),

                    ..default()
                },
                background_color: Color::NONE.into(),
                ..default()
            },
            BuildingMovementButton::Right,
        ))
        .with_children(|parent| {
            parent.spawn(AtlasImageBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    height: Val::Percent(100.0),
                    ..default()
                },
                texture_atlas: app_assets.ui_gradient.clone(),
                texture_atlas_image: UiTextureAtlasImage {
                    index: 0,
                    ..default()
                },
                background_color: Color::BLACK.into(),
                ..default()
            });
        });
}
