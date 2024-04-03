use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::{
    assets::AppAssets,
    constants::ITEM_PRICE_MULTIPLIER,
    game::{
        basement::building::{BuildingCharacter, Buildings},
        PlayerData,
    },
    style::{DARKER_MAIN_COLOR, DARK_MAIN_COLOR, MAIN_COLOR},
};

#[derive(Component)]
pub enum MultiplierButtonComponent {
    X1,
    X10,
}

impl MultiplierButtonComponent {
    pub fn as_usize(&self) -> usize {
        match &self {
            MultiplierButtonComponent::X1 => 1,
            MultiplierButtonComponent::X10 => 10,
        }
    }
}

#[derive(Component)]
pub enum ControlButtonComponent {
    Buy,
    Sell,
}

pub fn generate_shop_ui(
    mut commands: Commands,
    savegame: Res<Persistent<PlayerData>>,
    buildings: Res<Buildings>,
    app_assets: Res<AppAssets>,
) {
    let mut shop_item_ids: Vec<Entity> = Vec::new();
    let buildings = &buildings.0;

    for b in buildings.iter() {
        let price = match savegame.buildings.get(&b.building) {
            Some(x) => b.price * ITEM_PRICE_MULTIPLIER.powf(*x as f64) as f32,
            None => b.price,
        };

        let id = commands
            .spawn(NodeBundle {
                style: Style {
                    display: Display::Flex,
                    flex_direction: FlexDirection::Column,
                    width: Val::Percent(100.0),
                    padding: UiRect::all(Val::Percent(1.0)),
                    margin: UiRect::vertical(Val::Percent(1.0)),
                    ..default()
                },
                background_color: DARK_MAIN_COLOR.into(),
                ..default()
            })
            .with_children(|parent| {
                // Info
                parent
                    .spawn(NodeBundle {
                        style: Style {
                            display: Display::Flex,
                            flex_direction: FlexDirection::Row,
                            align_items: AlignItems::Center,
                            ..default()
                        },
                        ..default()
                    })
                    .with_children(|info| {
                        let icon_style = Style {
                            width: Val::Px(64.0),
                            aspect_ratio: Some(1.0),
                            ..default()
                        };

                        // Item icon
                        match b.building.get_image_handles(&app_assets).1 {
                            BuildingCharacter::Static(v) => {
                                info.spawn(ImageBundle {
                                    style: icon_style,
                                    image: UiImage::new(v),
                                    ..default()
                                });
                            }
                            BuildingCharacter::Animated(v, a) => {
                                info.spawn((
                                    AtlasImageBundle {
                                        style: icon_style,
                                        texture_atlas: v,
                                        ..default()
                                    },
                                    a,
                                ));
                            }
                        }

                        // Item description
                        info.spawn(NodeBundle {
                            style: Style {
                                display: Display::Flex,
                                flex_direction: FlexDirection::Column,
                                flex_grow: 1.0,
                                ..default()
                            },
                            ..default()
                        })
                        .with_children(|desc| {
                            // Item name
                            desc.spawn(TextBundle::from_section(
                                b.building.to_string(),
                                TextStyle {
                                    font: app_assets.font_text.clone(),
                                    font_size: 18.0,
                                    color: Color::BLACK.into(),
                                },
                            ));

                            // Item price
                            desc.spawn(TextBundle::from_section(
                                price.trunc().to_string(),
                                TextStyle {
                                    font: app_assets.font_text.clone(),
                                    font_size: 20.0,
                                    color: Color::BEIGE.into(),
                                },
                            ));
                        });
                    });

                // Control buttons
                parent
                    .spawn(NodeBundle {
                        style: Style {
                            display: Display::Flex,
                            flex_direction: FlexDirection::Row,
                            align_items: AlignItems::Center,
                            margin: UiRect::top(Val::Percent(1.0)),
                            ..default()
                        },
                        ..default()
                    })
                    .with_children(|ctrl| {
                        let button_style = Style {
                            display: Display::Flex,
                            justify_content: JustifyContent::Center,
                            align_items: AlignItems::Center,
                            padding: UiRect::all(Val::Percent(2.0)),
                            flex_grow: 1.0,
                            ..default()
                        };

                        let text_style = TextStyle {
                            font: app_assets.font_text.clone(),
                            font_size: 12.0,
                            color: Color::BEIGE.into(),
                        };

                        let text_node_style = Style {
                            width: Val::Percent(100.0),
                            ..default()
                        };

                        // Buy 1x
                        ctrl.spawn((
                            ButtonBundle {
                                style: Style {
                                    margin: UiRect::right(Val::Percent(2.0)),
                                    ..button_style.clone()
                                },
                                background_color: Color::GRAY.into(),
                                ..default()
                            },
                            ControlButtonComponent::Buy,
                            MultiplierButtonComponent::X1,
                            b.building.clone(),
                        ))
                        .with_children(|btn| {
                            btn.spawn(
                                TextBundle::from_section("Buy 1x", text_style.clone())
                                    .with_style(text_node_style.clone())
                                    .with_text_alignment(TextAlignment::Center),
                            );
                        });

                        // Buy 10x
                        ctrl.spawn((
                            ButtonBundle {
                                style: Style {
                                    margin: UiRect::right(Val::Percent(2.0)),
                                    ..button_style.clone()
                                },
                                background_color: Color::GRAY.into(),
                                ..default()
                            },
                            ControlButtonComponent::Buy,
                            MultiplierButtonComponent::X10,
                            b.building.clone(),
                        ))
                        .with_children(|btn| {
                            btn.spawn(
                                TextBundle::from_section("Buy 10x", text_style.clone())
                                    .with_style(text_node_style.clone())
                                    .with_text_alignment(TextAlignment::Center),
                            );
                        });

                        // Sell 1x
                        ctrl.spawn((
                            ButtonBundle {
                                style: Style {
                                    margin: UiRect::right(Val::Percent(2.0)),
                                    ..button_style.clone()
                                },
                                background_color: Color::GRAY.into(),
                                ..default()
                            },
                            ControlButtonComponent::Sell,
                            MultiplierButtonComponent::X1,
                            b.building.clone(),
                        ))
                        .with_children(|btn| {
                            btn.spawn(
                                TextBundle::from_section("Sell 1x", text_style.clone())
                                    .with_style(text_node_style.clone())
                                    .with_text_alignment(TextAlignment::Center),
                            );
                        });

                        // Sell 10x
                        ctrl.spawn((
                            ButtonBundle {
                                style: button_style.clone(),
                                background_color: Color::GRAY.into(),
                                ..default()
                            },
                            ControlButtonComponent::Sell,
                            MultiplierButtonComponent::X10,
                            b.building.clone(),
                        ))
                        .with_children(|btn| {
                            btn.spawn(
                                TextBundle::from_section("Sell 10x", text_style.clone())
                                    .with_style(text_node_style.clone())
                                    .with_text_alignment(TextAlignment::Center),
                            );
                        });
                    });
            })
            .id();

        shop_item_ids.push(id);
    }

    commands
        .spawn((
            NodeBundle {
                style: Style {
                    position_type: PositionType::Absolute,
                    display: Display::Flex,
                    flex_direction: FlexDirection::Column,
                    left: Val::Percent(75.0),
                    bottom: Val::Percent(15.0),
                    width: Val::Percent(25.0),
                    height: Val::Percent(85.0),
                    ..default()
                },
                ..default()
            },
            Name::new("Shop UI"),
        ))
        .with_children(|root| {
            root.spawn(NodeBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    padding: UiRect::all(Val::Percent(5.0)),
                    display: Display::Flex,
                    justify_content: JustifyContent::Center,
                    align_items: AlignItems::Center,
                    ..default()
                },
                background_color: MAIN_COLOR.into(),
                ..default()
            })
            .with_children(|panel| {
                panel.spawn(TextBundle {
                    text: Text::from_section(
                        "Shop",
                        TextStyle {
                            font: app_assets.font_text.clone(),
                            font_size: 48.0,
                            color: Color::BLACK.into(),
                        },
                    ),
                    ..default()
                });
            });

            let mut shop_item_list = root.spawn(NodeBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    flex_grow: 3.0,
                    display: Display::Flex,
                    flex_direction: FlexDirection::Column,
                    ..default()
                },
                background_color: DARKER_MAIN_COLOR.into(),
                ..default()
            });

            for e in shop_item_ids {
                shop_item_list.add_child(e);
            }
        });
}
