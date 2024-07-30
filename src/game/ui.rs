use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::{menu::ui::MenuControlComponent, persistent::Settings, GUIAssets};

use super::components::GameObjectComponent;

pub fn setup_ui(
    mut commands: Commands,
    gui_assets: Res<GUIAssets>,
    settings: Res<Persistent<Settings>>,
) {
    commands
        .spawn((
            NodeBundle {
                style: Style {
                    position_type: PositionType::Absolute,
                    left: Val::Percent(25.0),
                    top: Val::Percent(0.0),
                    display: Display::Flex,
                    flex_direction: FlexDirection::Row,
                    align_items: AlignItems::Center,
                    padding: UiRect::all(Val::Percent(1.0)),
                    column_gap: Val::Percent(5.0),
                    ..default()
                },
                ..default()
            },
            Name::new("Control buttons"),
            GameObjectComponent,
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
                MenuControlComponent::GameBack,
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
