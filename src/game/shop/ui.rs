use bevy::{color::palettes::css as color, prelude::*};
use bevy_persistent::Persistent;

use crate::{
    assets::FontAssets, game::components::GameObjectComponent, persistent::Savegame, style::*,
    GUIAssets,
};

use super::{ShopMode, ShopMultiplier};

pub fn setup_ui(
    mut commands: Commands,
    font_assets: Res<FontAssets>,
    gui_assets: Res<GUIAssets>,
    savegame: Res<Persistent<Savegame>>,
) {
    commands
        .spawn((
            NodeBundle {
                style: Style {
                    width: Val::Percent(25.0),
                    height: Val::Percent(100.0),
                    display: Display::Flex,
                    flex_direction: FlexDirection::Column,
                    ..default()
                },
                background_color: STORE_BG_COLOR.into(),
                ..default()
            },
            GameObjectComponent,
            Name::new("Shop UI"),
        ))
        .with_children(|root| {
            // Shop title
            root.spawn((
                NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        display: Display::Flex,
                        justify_content: JustifyContent::Center,
                        align_items: AlignItems::Center,
                        ..default()
                    },
                    ..default()
                },
                Name::new("Shop title"),
            ))
            .with_children(|title_root| {
                title_root.spawn((
                    TextBundle::from_section("Store", get_text_style_header(&font_assets)),
                    Name::new("Title"),
                ));
            });

            // Shop control
            root.spawn((
                NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        display: Display::Flex,
                        flex_direction: FlexDirection::Row,
                        padding: UiRect::all(Val::Percent(2.0)),
                        ..default()
                    },
                    background_color: STORE_CONTROL_BG_COLOR.into(),
                    ..default()
                },
                Name::new("Shop control"),
            ))
            .with_children(|control_root| {
                // Mode control
                control_root
                    .spawn((
                        NodeBundle {
                            style: Style {
                                min_width: Val::Percent(50.0),
                                display: Display::Flex,
                                flex_direction: FlexDirection::Column,
                                margin: UiRect::right(Val::Percent(2.0)),
                                ..default()
                            },
                            ..default()
                        },
                        Name::new("Mode control"),
                    ))
                    .with_children(|mode_root| {
                        let button = ButtonBundle {
                            style: Style {
                                display: Display::Flex,
                                justify_content: JustifyContent::Center,
                                align_items: AlignItems::Center,
                                flex_grow: 1.0,
                                padding: UiRect::all(Val::Percent(2.0)),
                                margin: UiRect::bottom(Val::Percent(4.0)),
                                ..default()
                            },
                            background_color: color::PERU.into(),
                            ..default()
                        };

                        // Buy button
                        mode_root
                            .spawn((button.clone(), ShopMode::Buy, Name::new("Buy button")))
                            .with_children(|btn| {
                                btn.spawn(TextBundle::from_section(
                                    "Buy",
                                    get_text_style_default(&font_assets),
                                ));
                            });

                        // Sell button
                        mode_root
                            .spawn((
                                {
                                    let mut b = button.clone();
                                    b.style.margin.bottom = Val::ZERO;
                                    b
                                },
                                ShopMode::Sell,
                                Name::new("Sell button"),
                            ))
                            .with_children(|btn| {
                                btn.spawn(TextBundle::from_section(
                                    "Sell",
                                    get_text_style_default(&font_assets),
                                ));
                            });
                    });

                // Multiplier control
                control_root
                    .spawn((
                        NodeBundle {
                            style: Style {
                                display: Display::Flex,
                                flex_direction: FlexDirection::Row,
                                ..default()
                            },
                            ..default()
                        },
                        Name::new("Multiplier control"),
                    ))
                    .with_children(|mp_root| {
                        let button = ButtonBundle {
                            style: Style {
                                height: Val::Percent(100.0),
                                display: Display::Flex,
                                justify_content: JustifyContent::Center,
                                align_items: AlignItems::Center,
                                aspect_ratio: Some(1.0),
                                margin: UiRect::right(Val::Percent(4.0)),
                                ..default()
                            },
                            background_color: color::PERU.into(),
                            ..default()
                        };

                        // 1x button
                        mp_root
                            .spawn((button.clone(), ShopMultiplier::X1, Name::new("1x button")))
                            .with_children(|btn| {
                                btn.spawn(TextBundle::from_section(
                                    "1X",
                                    get_text_style_default(&font_assets),
                                ));
                            });

                        // 10x button
                        mp_root
                            .spawn((button.clone(), ShopMultiplier::X10, Name::new("10x button")))
                            .with_children(|btn| {
                                btn.spawn(TextBundle::from_section(
                                    "10X",
                                    get_text_style_default(&font_assets),
                                ));
                            });
                    });
            });

            // Shop list
            root.spawn((
                NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        display: Display::Flex,
                        flex_direction: FlexDirection::Column,
                        flex_grow: 1.0,
                        ..default()
                    },
                    background_color: STORE_LIST_BG_COLOR.into(),
                    ..default()
                },
                Name::new("Shop list"),
            ));

            // Player stats
            root.spawn((
                NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        display: Display::Flex,
                        flex_direction: FlexDirection::Row,
                        padding: UiRect::all(Val::Percent(2.0)),
                        ..default()
                    },
                    ..default()
                },
                Name::new("Player stats"),
            ))
            .with_children(|stats_root| {
                let node = NodeBundle {
                    style: Style {
                        flex_grow: 1.0,
                        display: Display::Flex,
                        align_items: AlignItems::Center,
                        flex_direction: FlexDirection::Row,
                        ..default()
                    },
                    ..default()
                };

                let icon_style = Style {
                    width: Val::Percent(25.0),
                    margin: UiRect::right(Val::Percent(5.0)),
                    ..default()
                };

                // Money
                stats_root
                    .spawn((node.clone(), Name::new("Money")))
                    .with_children(|money_root| {
                        // Money icon
                        money_root.spawn((
                            ImageBundle {
                                style: icon_style.clone(),
                                image: UiImage::new(gui_assets.money.clone()),
                                ..default()
                            },
                            Name::new("Money icon"),
                        ));

                        // Money text
                        money_root.spawn((
                            TextBundle::from_section(
                                format!("{:.0}", savegame.money),
                                get_text_style_default(&font_assets),
                            ),
                            Name::new("Money text"),
                        ));
                    });

                // Multiplier
                stats_root
                    .spawn((node.clone(), Name::new("Multiplier")))
                    .with_children(|multiplier_root| {
                        // Multiplier icon
                        multiplier_root.spawn((
                            ImageBundle {
                                style: icon_style,
                                image: UiImage::new(gui_assets.multiplier.clone()),
                                ..default()
                            },
                            Name::new("Multiplier icon"),
                        ));

                        // Multiplier text
                        multiplier_root.spawn((
                            TextBundle::from_section(
                                format!("{:.1}", savegame.multiplier),
                                get_text_style_default(&font_assets),
                            ),
                            Name::new("Multiplier text"),
                        ));
                    });
            });
        });
}
