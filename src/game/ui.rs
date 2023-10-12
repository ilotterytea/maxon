use bevy::prelude::*;

use super::player::PlayerData;

#[derive(Component)]
pub struct UiTextMoneyComponent;

pub fn generate_ui(mut commands: Commands, player_data: Res<PlayerData>) {
    commands
        .spawn(NodeBundle {
            style: Style {
                width: Val::Percent(100.0),
                height: Val::Percent(100.0),
                display: Display::Flex,
                flex_direction: FlexDirection::Column,
                ..default()
            },
            background_color: Color::NONE.into(),
            ..default()
        })
        .with_children(|parent| {
            // Player stats
            parent
                .spawn(NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        padding: UiRect::all(Val::Percent(0.5)),
                        display: Display::Flex,
                        flex_direction: FlexDirection::Row,
                        align_items: AlignItems::Center,
                        ..default()
                    },
                    ..default()
                })
                .with_children(|parent| {
                    parent.spawn((
                        TextBundle {
                            text: Text::from_sections([TextSection::new(
                                player_data.money.to_string(),
                                TextStyle::default(),
                            )]),
                            ..default()
                        },
                        UiTextMoneyComponent,
                    ));
                });
            // Main
            parent
                .spawn(NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        flex_grow: 1.0,
                        display: Display::Flex,
                        flex_direction: FlexDirection::Row,
                        align_items: AlignItems::Center,
                        ..default()
                    },
                    ..default()
                })
                // Shop
                .with_children(|parent| {
                    parent.spawn(NodeBundle {
                        style: Style {
                            height: Val::Percent(100.0),
                            padding: UiRect::all(Val::Percent(1.0)),
                            display: Display::Flex,
                            flex_direction: FlexDirection::Column,
                            ..default()
                        },
                        ..default()
                    });
                })
                // empty space
                .with_children(|parent| {
                    parent.spawn(NodeBundle {
                        style: Style {
                            height: Val::Percent(100.0),
                            flex_grow: 1.0,
                            ..default()
                        },
                        ..default()
                    });
                });
        });
}

pub fn update_ui(
    player_data: Res<PlayerData>,
    mut money_text_query: Query<&mut Text, With<UiTextMoneyComponent>>,
) {
    if let Ok(mut money_text) = money_text_query.get_single_mut() {
        if let Some(mut text) = money_text.sections.get_mut(0) {
            text.value = player_data.money.to_string();
        }
    }
}
