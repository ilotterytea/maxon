use bevy::{color::palettes::css as color, prelude::*};
use bevy_persistent::Persistent;

use crate::{
    localization::{LineId, LocalizationManager},
    persistent::{Savegame, Settings},
    style::{get_text_style_default, STORE_ITEM_BG_COLOR, STORE_LIST_BG_COLOR},
    FontAssets, GUIAssets,
};

use super::systems::MenuObjectComponent;

#[derive(Component)]
pub enum MenuControlComponent {
    Exit,
    Music,
    Fullscreen,
    Language,
}

pub fn setup_ui(
    mut commands: Commands,
    savegame: Res<Persistent<Savegame>>,
    settings: Res<Persistent<Settings>>,
    gui_assets: Res<GUIAssets>,
    font_assets: Res<FontAssets>,
    localization: Res<LocalizationManager>,
) {
    commands
        .spawn((
            NodeBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    height: Val::Percent(100.0),
                    display: Display::Flex,
                    flex_direction: FlexDirection::Column,
                    padding: UiRect::all(Val::Percent(2.0)),
                    ..default()
                },
                ..default()
            },
            Name::new("Menu UI"),
            MenuObjectComponent,
        ))
        .with_children(|root| {
            // Logo
            root.spawn((
                NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        flex_grow: 4.0,
                        display: Display::Flex,
                        justify_content: JustifyContent::Center,
                        align_items: AlignItems::Center,
                        ..default()
                    },
                    ..default()
                },
                Name::new("Logo node"),
            ))
            .with_children(|node| {
                node.spawn((
                    ImageBundle {
                        image: UiImage::new(gui_assets.logo.clone()),
                        ..default()
                    },
                    Name::new("Logo"),
                ));
            });

            // Savegame
            root.spawn((
                NodeBundle {
                    style: Style {
                        display: Display::Flex,
                        margin: UiRect::vertical(Val::Percent(1.0)),
                        align_items: AlignItems::Center,
                        justify_content: JustifyContent::Center,
                        flex_grow: 2.0,
                        ..default()
                    },
                    ..default()
                },
                Name::new("Savegame node"),
            ))
            .with_children(|node| {
                node.spawn((
                    NodeBundle {
                        style: Style {
                            display: Display::Flex,
                            flex_direction: FlexDirection::Column,
                            min_width: Val::Percent(50.0),
                            row_gap: Val::Percent(5.0),
                            ..default()
                        },
                        ..default()
                    },
                    Name::new("Savegame base"),
                ))
                .with_children(|base| {
                    // Data
                    base.spawn((
                        NodeBundle {
                            style: Style {
                                display: Display::Flex,
                                flex_direction: FlexDirection::Column,
                                border: UiRect::all(Val::Percent(0.5)),
                                padding: UiRect::all(Val::Percent(2.0)),
                                justify_content: JustifyContent::SpaceBetween,
                                min_height: Val::Px(100.0),
                                ..default()
                            },
                            border_color: STORE_LIST_BG_COLOR.into(),
                            border_radius: BorderRadius::all(Val::Percent(5.0)),
                            background_color: STORE_ITEM_BG_COLOR.into(),
                            ..default()
                        },
                        Name::new("Savegame data"),
                    ))
                    .with_children(|data| {
                        let style = Style {
                            width: Val::Percent(100.0),
                            display: Display::Flex,
                            flex_direction: FlexDirection::Row,
                            justify_content: JustifyContent::SpaceBetween,
                            ..default()
                        };

                        // Name and played time
                        data.spawn((
                            NodeBundle {
                                style: style.clone(),
                                ..default()
                            },
                            Name::new("Savegame name and played time"),
                        ))
                        .with_children(|node| {
                            // Name
                            node.spawn((
                                TextBundle::from_section(
                                    savegame.name.clone(),
                                    get_text_style_default(&font_assets),
                                ),
                                Name::new("Savegame name"),
                            ));

                            // Played time
                            node.spawn((
                                TextBundle::from_section(
                                    {
                                        let seconds = savegame.played_time % 60;
                                        let minutes = savegame.played_time / 60;
                                        fn pad(v: u32) -> String {
                                            format!("{}{}", if v <= 9 { "0" } else { "" }, v)
                                        }
                                        format!("{}:{}", pad(minutes), pad(seconds))
                                    },
                                    get_text_style_default(&font_assets),
                                ),
                                Name::new("Savegame played time"),
                            ));
                        });

                        // Money, multiplier, purchased pets
                        data.spawn((
                            NodeBundle {
                                style: style.clone(),
                                ..default()
                            },
                            Name::new("Money, multiplier and purchased pets"),
                        ))
                        .with_children(|node| {
                            let node_style = Style {
                                display: Display::Flex,
                                flex_direction: FlexDirection::Row,
                                align_items: AlignItems::Center,
                                column_gap: Val::Px(5.0),
                                ..default()
                            };

                            let icon_style = Style {
                                width: Val::Px(24.0),
                                height: Val::Px(24.0),
                                ..default()
                            };

                            // Money
                            node.spawn((
                                NodeBundle {
                                    style: node_style.clone(),
                                    ..default()
                                },
                                Name::new("Money node"),
                            ))
                            .with_children(|node| {
                                // Money icon
                                node.spawn((
                                    ImageBundle {
                                        style: icon_style.clone(),
                                        image: UiImage::new(gui_assets.money.clone()),
                                        ..default()
                                    },
                                    Name::new("Money icon"),
                                ));

                                // Money text
                                node.spawn((
                                    TextBundle::from_section(
                                        savegame.money.trunc().to_string(),
                                        get_text_style_default(&font_assets),
                                    ),
                                    Name::new("Money text"),
                                ));
                            });

                            // Multiplier
                            node.spawn((
                                NodeBundle {
                                    style: node_style.clone(),
                                    ..default()
                                },
                                Name::new("Multiplier node"),
                            ))
                            .with_children(|node| {
                                // Multiplier icon
                                node.spawn((
                                    ImageBundle {
                                        style: icon_style.clone(),
                                        image: UiImage::new(gui_assets.multiplier.clone()),
                                        ..default()
                                    },
                                    Name::new("Multiplier icon"),
                                ));

                                // Multiplier text
                                node.spawn((
                                    TextBundle::from_section(
                                        format!("{:.1}", savegame.multiplier),
                                        get_text_style_default(&font_assets),
                                    ),
                                    Name::new("Multiplier text"),
                                ));
                            });

                            // Pet
                            node.spawn((
                                NodeBundle {
                                    style: node_style,
                                    ..default()
                                },
                                Name::new("Pet node"),
                            ))
                            .with_children(|node| {
                                // Pet icon
                                node.spawn((
                                    ImageBundle {
                                        style: icon_style,
                                        image: UiImage::new(gui_assets.pets.clone()),
                                        ..default()
                                    },
                                    Name::new("Pet icon"),
                                ));

                                // Pet text
                                node.spawn((
                                    TextBundle::from_section(
                                        savegame.pets.values().sum::<u32>().to_string(),
                                        get_text_style_default(&font_assets),
                                    ),
                                    Name::new("Pet text"),
                                ));
                            });
                        });
                    });

                    // Control
                    base.spawn((
                        NodeBundle {
                            style: Style {
                                display: Display::Flex,
                                flex_direction: FlexDirection::Row,
                                column_gap: Val::Percent(1.5),
                                ..default()
                            },
                            ..default()
                        },
                        Name::new("Savegame control"),
                    ))
                    .with_children(|ctrl| {
                        // Continue button
                        ctrl.spawn((
                            ButtonBundle {
                                style: Style {
                                    padding: UiRect::all(Val::Percent(1.0)),
                                    flex_grow: 4.0,
                                    border: UiRect::all(Val::Percent(0.5)),
                                    justify_content: JustifyContent::Center,
                                    display: Display::Flex,
                                    ..default()
                                },
                                border_color: STORE_LIST_BG_COLOR.into(),
                                border_radius: BorderRadius::all(Val::Percent(5.0)),
                                background_color: color::PERU.into(),
                                ..default()
                            },
                            Name::new("Continue button"),
                        ))
                        .with_children(|btn| {
                            btn.spawn((
                                TextBundle::from_section(
                                    localization.get(LineId::MenuContinue),
                                    get_text_style_default(&font_assets),
                                ),
                                Name::new("Continue text"),
                            ));
                        });

                        // Reset button
                        ctrl.spawn((
                            ButtonBundle {
                                style: Style {
                                    padding: UiRect::all(Val::Percent(1.0)),
                                    flex_grow: 1.0,
                                    border: UiRect::all(Val::Percent(0.5)),
                                    justify_content: JustifyContent::Center,
                                    display: Display::Flex,
                                    ..default()
                                },
                                border_color: color::MAROON.into(),
                                border_radius: BorderRadius::all(Val::Percent(5.0)),
                                background_color: color::DARK_RED.into(),
                                ..default()
                            },
                            Name::new("Reset button"),
                        ))
                        .with_children(|btn| {
                            btn.spawn((
                                TextBundle::from_section(
                                    localization.get(LineId::MenuReset),
                                    get_text_style_default(&font_assets),
                                ),
                                Name::new("Reset text"),
                            ));
                        });
                    });
                });
            });

            let style = Style {
                display: Display::Flex,
                align_items: AlignItems::Center,
                flex_direction: FlexDirection::Row,
                ..default()
            };

            // Menu control
            root.spawn((
                NodeBundle {
                    style: Style {
                        justify_content: JustifyContent::SpaceBetween,
                        ..style.clone()
                    },
                    ..default()
                },
                Name::new("Menu control"),
            ))
            .with_children(|control| {
                // Left side
                control
                    .spawn((
                        NodeBundle {
                            style: style.clone(),
                            ..default()
                        },
                        Name::new("Left side menu control"),
                    ))
                    .with_children(|left| {
                        // Exit button
                        left.spawn((
                            ButtonBundle { ..default() },
                            MenuControlComponent::Exit,
                            Name::new("Exit button"),
                        ))
                        .with_children(|btn| {
                            btn.spawn((
                                ImageBundle {
                                    image: UiImage::new(gui_assets.exit.clone()),
                                    style: Style {
                                        width: Val::Percent(100.0),
                                        height: Val::Percent(100.0),
                                        ..default()
                                    },
                                    ..default()
                                },
                                Name::new("Exit icon"),
                            ));
                        });
                    });

                // Right side
                control
                    .spawn((
                        NodeBundle {
                            style: Style {
                                flex_grow: 1.0,
                                flex_direction: FlexDirection::RowReverse,
                                column_gap: Val::Percent(1.0),
                                ..style.clone()
                            },
                            ..default()
                        },
                        Name::new("Right side menu control"),
                    ))
                    .with_children(|right| {
                        // Music button
                        right
                            .spawn((
                                ButtonBundle { ..default() },
                                MenuControlComponent::Music,
                                Name::new("Music button"),
                            ))
                            .with_children(|btn| {
                                btn.spawn((
                                    ImageBundle {
                                        image: UiImage::new(if settings.music {
                                            gui_assets.music_on.clone()
                                        } else {
                                            gui_assets.music_off.clone()
                                        }),
                                        style: Style {
                                            width: Val::Percent(100.0),
                                            height: Val::Percent(100.0),
                                            ..default()
                                        },
                                        ..default()
                                    },
                                    Name::new("Music icon"),
                                ));
                            });

                        // Fullscreen button
                        right
                            .spawn((
                                ButtonBundle { ..default() },
                                MenuControlComponent::Fullscreen,
                                Name::new("Fullscreen button"),
                            ))
                            .with_children(|btn| {
                                btn.spawn((
                                    ImageBundle {
                                        image: UiImage::new(if settings.is_fullscreen {
                                            gui_assets.windowed.clone()
                                        } else {
                                            gui_assets.fullscreen.clone()
                                        }),
                                        style: Style {
                                            width: Val::Percent(100.0),
                                            height: Val::Percent(100.0),
                                            ..default()
                                        },
                                        ..default()
                                    },
                                    Name::new("Fullscreen icon"),
                                ));
                            });

                        // Language button
                        right
                            .spawn((
                                ButtonBundle { ..default() },
                                MenuControlComponent::Language,
                                Name::new("Language button"),
                            ))
                            .with_children(|btn| {
                                let mut handle: &Handle<Image> = &gui_assets.languages[0];

                                for lang in &gui_assets.languages {
                                    if let Some(path) = lang.path() {
                                        if let Some(name) = path.path().file_name() {
                                            let name = name
                                                .to_str()
                                                .unwrap()
                                                .strip_suffix(".png")
                                                .unwrap();

                                            if name.eq(settings.language.as_str()) {
                                                handle = lang;
                                                break;
                                            }
                                        }
                                    }
                                }

                                btn.spawn((
                                    ImageBundle {
                                        image: UiImage::new(handle.clone()),
                                        style: Style {
                                            width: Val::Percent(100.0),
                                            height: Val::Percent(100.0),
                                            ..default()
                                        },
                                        ..default()
                                    },
                                    Name::new("Language icon"),
                                ));
                            });
                    });
            });
        });
}
