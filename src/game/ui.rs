use crate::{
    assets::AppAssets,
    localization::{LineId, Localization},
    style::{
        get_category_header_text_style, get_item_header_text_style, ITEM_BG_ACTIVE_COLOR,
        ITEM_BORDER_COLOR,
    },
};
use bevy::prelude::*;

use super::{
    item::{ItemComponent, Items},
    player::PlayerData,
};

#[derive(Component)]
pub struct UiTextMoneyComponent;

#[derive(Component)]
pub struct UiTextItemCostComponent(pub String);

#[derive(Component)]
pub struct UiTextItemHeaderComponent(pub String);

#[derive(Component)]
pub struct UiTextItemIconComponent(pub String);

pub fn generate_ui(
    mut commands: Commands,
    player_data: Res<PlayerData>,
    items: Res<Items>,
    app_assets: Res<AppAssets>,
    locales: Res<Assets<Localization>>,
) {
    let locale = locales.get(&app_assets.locale_english).unwrap();

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
                    parent
                        .spawn(NodeBundle {
                            style: Style {
                                height: Val::Percent(100.0),
                                //padding: UiRect::all(Val::Percent(1.0)),
                                display: Display::Flex,
                                flex_grow: 1.0,
                                flex_direction: FlexDirection::Column,
                                ..default()
                            },
                            background_color: Color::PINK.into(),
                            ..default()
                        })
                        // Shop Header
                        .with_children(|parent| {
                            parent.spawn(
                                TextBundle {
                                    style: Style {
                                        align_self: AlignSelf::Center,
                                        margin: UiRect::all(Val::Percent(1.0)),
                                        ..default()
                                    },
                                    text: Text::from_section(
                                        locale
                                            .get_literal_line(LineId::CategoryShopHeader)
                                            .unwrap(),
                                        get_category_header_text_style(
                                            app_assets.font_text.clone(),
                                        ),
                                    ),
                                    ..default()
                                }
                                .with_text_alignment(TextAlignment::Center),
                            );
                        })
                        // Shop Items
                        .with_children(|parent| {
                            for item in items.0.iter() {
                                parent
                                    .spawn((
                                        ButtonBundle {
                                            style: Style {
                                                display: Display::Flex,
                                                flex_direction: FlexDirection::Row,
                                                align_items: AlignItems::Center,
                                                width: Val::Percent(100.0),
                                                min_height: Val::Percent(3.0),
                                                //margin: UiRect::all(Val::Px(5.0)),
                                                border: UiRect::bottom(Val::Percent(1.5)),
                                                ..default()
                                            },
                                            border_color: ITEM_BORDER_COLOR.into(),
                                            background_color: ITEM_BG_ACTIVE_COLOR.into(),
                                            ..default()
                                        },
                                        ItemComponent(item.id.clone()),
                                    ))
                                    // Icon
                                    .with_children(|parent| {
                                        parent.spawn((
                                            ImageBundle {
                                                style: Style {
                                                    max_width: Val::Percent(15.0),
                                                    ..default()
                                                },
                                                image: UiImage::new(app_assets.icon.clone()),
                                                ..default()
                                            },
                                            UiTextItemIconComponent(item.id.clone()),
                                        ));
                                    })
                                    // Information
                                    .with_children(|parent| {
                                        parent
                                            .spawn(NodeBundle {
                                                style: Style {
                                                    display: Display::Flex,
                                                    flex_direction: FlexDirection::Column,
                                                    flex_grow: 1.0,
                                                    ..default()
                                                },
                                                ..default()
                                            })
                                            // Label
                                            .with_children(|parent| {
                                                parent.spawn((
                                                    TextBundle {
                                                        style: Style {
                                                            flex_grow: 1.0,
                                                            width: Val::Percent(100.0),
                                                            margin: UiRect::all(Val::Percent(1.0)),
                                                            ..default()
                                                        },
                                                        text: Text::from_section(
                                                            locale
                                                                .get_literal_line(LineId::ItemBror)
                                                                .unwrap(),
                                                            get_item_header_text_style(
                                                                app_assets.font_text.clone(),
                                                            ),
                                                        ),
                                                        ..default()
                                                    },
                                                    UiTextItemHeaderComponent(item.id.clone()),
                                                ));
                                            })
                                            // Price
                                            .with_children(|parent| {
                                                parent.spawn((
                                                    TextBundle {
                                                        style: Style {
                                                            flex_grow: 2.0,
                                                            width: Val::Percent(100.0),
                                                            margin: UiRect::all(Val::Percent(1.0)),
                                                            ..default()
                                                        },
                                                        text: Text::from_section(
                                                            item.price.to_string(),
                                                            get_item_header_text_style(
                                                                app_assets.font_text.clone(),
                                                            ),
                                                        ),
                                                        ..default()
                                                    },
                                                    UiTextItemCostComponent(item.id.clone()),
                                                ));
                                            });
                                    });
                            }
                        });
                })
                // empty space
                .with_children(|parent| {
                    parent.spawn(NodeBundle {
                        style: Style {
                            height: Val::Percent(100.0),
                            flex_grow: 4.0,
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
