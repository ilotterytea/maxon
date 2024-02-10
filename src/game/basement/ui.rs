use bevy::prelude::*;

#[derive(Component, PartialEq, Eq, PartialOrd, Ord)]
pub(super) enum BuildingMovementButton {
    Left,
    Right,
}

pub(super) fn building_movement_buttons(mut commands: Commands) {
    commands.spawn((
        ButtonBundle {
            style: Style {
                position_type: PositionType::Absolute,
                bottom: Val::Percent(15.0),
                left: Val::Percent(0.0),
                height: Val::Percent(100.0),
                width: Val::Percent(15.0),
                ..default()
            },
            ..default()
        },
        BuildingMovementButton::Left,
    ));

    commands.spawn((
        ButtonBundle {
            style: Style {
                position_type: PositionType::Absolute,
                bottom: Val::Percent(15.0),
                right: Val::Percent(0.0),
                height: Val::Percent(100.0),
                width: Val::Percent(15.0),

                ..default()
            },
            ..default()
        },
        BuildingMovementButton::Right,
    ));
}
