use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::{
    assets::AppAssets,
    constants::ITEM_PRICE_MULTIPLIER,
    game::{
        basement::building::{Building, BuildingCharacter, Buildings},
        PlayerData,
    },
    style::{DARKER_MAIN_COLOR, DARK_MAIN_COLOR, MAIN_COLOR},
};

use super::{ShopMode, ShopMultiplier, ShopSettings};

#[derive(Component)]
pub struct UiUnitComponent;

#[derive(Component)]
pub struct UiUnitItemInfoComponent;

#[derive(Component)]
pub struct UiUnitItemDescComponent;

#[derive(Component)]
pub struct UiUnitPriceTextComponent;

#[derive(Component)]
pub struct UiUnitDisabledComponent;

#[derive(Component)]
pub struct UiShopComponent;

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
            .spawn((
                ButtonBundle {
                    style: Style {
                        display: Display::Flex,
                        flex_direction: FlexDirection::Column,
                        width: Val::Percent(100.0),
                        margin: UiRect::vertical(Val::Percent(1.0)),
                        ..default()
                    },
                    background_color: DARK_MAIN_COLOR.into(),
                    ..default()
                },
                b.building.clone(),
                UiUnitComponent,
                Name::new(format!("Shop slot: {}", b.building.to_string())),
            ))
            .with_children(|parent| {
                parent.spawn((
                    ImageBundle {
                        style: Style {
                            position_type: PositionType::Absolute,
                            width: Val::Percent(100.0),
                            height: Val::Percent(100.0),
                            ..default()
                        },
                        background_color: DARK_MAIN_COLOR.into(),
                        image: UiImage::new(app_assets.ui_radial_gradient.clone()),
                        ..default()
                    },
                    Name::new(format!("Shop bg gradient: {}", b.building.to_string())),
                ));
                // Info
                parent
                    .spawn((
                        NodeBundle {
                            style: Style {
                                display: Display::Flex,
                                flex_direction: FlexDirection::Row,
                                align_items: AlignItems::Center,
                                ..default()
                            },
                            ..default()
                        },
                        UiUnitItemInfoComponent,
                        Name::new(format!("Shop slot info: {}", b.building.to_string())),
                    ))
                    .with_children(|info| {
                        let icon_style = Style {
                            width: Val::Px(64.0),
                            aspect_ratio: Some(1.0),
                            ..default()
                        };

                        // Item icon
                        match b.building.get_image_handles(&app_assets).1 {
                            BuildingCharacter::Static(v) => {
                                info.spawn((
                                    ImageBundle {
                                        style: icon_style,
                                        image: UiImage::new(v),
                                        ..default()
                                    },
                                    Name::new(format!(
                                        "Shop slot icon: {}",
                                        b.building.to_string()
                                    )),
                                ));
                            }
                            BuildingCharacter::Animated(v, a) => {
                                info.spawn((
                                    AtlasImageBundle {
                                        style: icon_style,
                                        texture_atlas: v,
                                        ..default()
                                    },
                                    a,
                                    Name::new(format!(
                                        "Shop slot icon: {}",
                                        b.building.to_string()
                                    )),
                                ));
                            }
                        }

                        // Item description
                        info.spawn((
                            NodeBundle {
                                style: Style {
                                    display: Display::Flex,
                                    flex_direction: FlexDirection::Column,
                                    flex_grow: 1.0,
                                    ..default()
                                },
                                ..default()
                            },
                            UiUnitItemDescComponent,
                            Name::new(format!("Shop slot desc: {}", b.building.to_string())),
                        ))
                        .with_children(|desc| {
                            // Item name
                            desc.spawn((
                                TextBundle::from_section(
                                    b.building.to_string(),
                                    TextStyle {
                                        font: app_assets.font_text.clone(),
                                        font_size: 18.0,
                                        color: Color::BLACK.into(),
                                    },
                                ),
                                Name::new(format!("Shop slot name: {}", b.building.to_string())),
                            ));

                            // Item price
                            desc.spawn((
                                TextBundle::from_section(
                                    price.trunc().to_string(),
                                    TextStyle {
                                        font: app_assets.font_text.clone(),
                                        font_size: 20.0,
                                        color: Color::BEIGE.into(),
                                    },
                                ),
                                Name::new(format!("Shop slot price: {}", b.building.to_string())),
                                UiUnitPriceTextComponent,
                            ));
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
            UiShopComponent,
        ))
        .with_children(|root| {
            root.spawn((
                NodeBundle {
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
                },
                Name::new("Shop title"),
            ))
            .with_children(|panel| {
                panel.spawn((
                    TextBundle {
                        text: Text::from_section(
                            "Shop",
                            TextStyle {
                                font: app_assets.font_text.clone(),
                                font_size: 48.0,
                                color: Color::BLACK.into(),
                            },
                        ),
                        ..default()
                    },
                    Name::new("Shop title text"),
                ));
            });

            // Shop settings buttons
            root.spawn((
                NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        display: Display::Flex,
                        flex_direction: FlexDirection::Row,
                        align_items: AlignItems::Center,
                        justify_content: JustifyContent::Center,
                        padding: UiRect::all(Val::Percent(1.0)),
                        ..default()
                    },
                    background_color: MAIN_COLOR.into(),
                    ..default()
                },
                Name::new("Shop settings"),
            ))
            .with_children(|panel| {
                let text_style = TextStyle {
                    font: app_assets.font_text.clone(),
                    font_size: 14.0,
                    color: Color::WHITE,
                };

                let btn_style = Style {
                    width: Val::Percent(100.0),
                    flex_grow: 1.0,
                    padding: UiRect::all(Val::Percent(2.0)),
                    display: Display::Flex,
                    align_items: AlignItems::Center,
                    justify_content: JustifyContent::Center,
                    ..default()
                };

                // Shop mode buttons
                panel
                    .spawn((
                        NodeBundle {
                            style: Style {
                                height: Val::Percent(100.0),
                                display: Display::Flex,
                                flex_grow: 1.0,
                                justify_content: JustifyContent::Center,
                                align_items: AlignItems::Center,
                                flex_direction: FlexDirection::Column,
                                ..default()
                            },
                            ..default()
                        },
                        Name::new("Shop mode"),
                    ))
                    .with_children(|panel| {
                        panel
                            .spawn((
                                ButtonBundle {
                                    style: btn_style.clone(),
                                    background_color: Color::NONE.into(),
                                    ..default()
                                },
                                ShopMode::Buy,
                                Name::new("Shop mode: buy"),
                            ))
                            .with_children(|btn| {
                                btn.spawn((
                                    TextBundle::from_section("Buy", text_style.clone()),
                                    Name::new("Shop mode text: buy"),
                                ));
                            });

                        panel
                            .spawn((
                                ButtonBundle {
                                    style: btn_style.clone(),
                                    background_color: Color::NONE.into(),
                                    ..default()
                                },
                                ShopMode::Sell,
                                Name::new("Shop mode: sell"),
                            ))
                            .with_children(|btn| {
                                btn.spawn((
                                    TextBundle::from_section("Sell", text_style.clone()),
                                    Name::new("Shop mode text: sell"),
                                ));
                            });
                    });

                let sell_btn_style = Style {
                    height: Val::Percent(100.0),
                    aspect_ratio: Some(1.0),
                    margin: UiRect::horizontal(Val::Percent(1.0)),
                    padding: UiRect::all(Val::Percent(5.0)),
                    display: Display::Flex,
                    justify_content: JustifyContent::Center,
                    align_items: AlignItems::Center,
                    ..default()
                };

                // Shop multiplier buttons
                panel
                    .spawn((
                        NodeBundle {
                            style: Style {
                                height: Val::Percent(100.0),
                                display: Display::Flex,
                                flex_grow: 2.0,
                                align_items: AlignItems::Center,
                                flex_direction: FlexDirection::Row,
                                ..default()
                            },
                            background_color: Color::NONE.into(),
                            ..default()
                        },
                        Name::new("Shop multiplier"),
                    ))
                    .with_children(|panel| {
                        panel
                            .spawn((
                                ButtonBundle {
                                    style: sell_btn_style.clone(),
                                    background_color: Color::DARK_GRAY.into(),
                                    ..default()
                                },
                                ShopMultiplier::X1,
                                Name::new("Shop multiplier: 1x"),
                            ))
                            .with_children(|btn| {
                                btn.spawn((
                                    TextBundle::from_section("1x", text_style.clone()),
                                    Name::new("Shop multiplier text: 1x"),
                                ));
                            });

                        panel
                            .spawn((
                                ButtonBundle {
                                    style: sell_btn_style.clone(),
                                    background_color: Color::DARK_GRAY.into(),
                                    ..default()
                                },
                                ShopMultiplier::X10,
                                Name::new("Shop multiplier: 10x"),
                            ))
                            .with_children(|btn| {
                                btn.spawn((
                                    TextBundle::from_section("10x", text_style.clone()),
                                    Name::new("Shop multiplier text: 10x"),
                                ));
                            });
                    });
            });

            let mut shop_item_list = root.spawn((
                NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        flex_grow: 3.0,
                        display: Display::Flex,
                        flex_direction: FlexDirection::Column,
                        ..default()
                    },
                    background_color: DARKER_MAIN_COLOR.into(),
                    ..default()
                },
                Name::new("Shop item list"),
            ));

            for e in shop_item_ids {
                shop_item_list.add_child(e);
            }
        });
}

pub fn update_price(
    unit_query: Query<(&Building, &Children), With<UiUnitComponent>>,
    info_query: Query<
        (Entity, &Children),
        (
            With<UiUnitItemInfoComponent>,
            Without<UiUnitPriceTextComponent>,
            Without<UiUnitItemDescComponent>,
            Without<UiUnitComponent>,
        ),
    >,
    desc_query: Query<
        (Entity, &Children),
        (
            With<UiUnitItemDescComponent>,
            Without<UiUnitPriceTextComponent>,
            Without<UiUnitItemInfoComponent>,
            Without<UiUnitComponent>,
        ),
    >,
    mut text_query: Query<
        (Entity, &mut Text),
        (
            With<UiUnitPriceTextComponent>,
            Without<UiUnitItemDescComponent>,
            Without<UiUnitItemInfoComponent>,
        ),
    >,
    savegame: Res<Persistent<PlayerData>>,
    shop_settings: Res<ShopSettings>,
    buildings: Res<Buildings>,
) {
    let unit_amount = shop_settings.multiplier.as_usize() as f64;

    let buildings = &buildings.0;

    for (unit_b, unit_c) in unit_query.iter() {
        let building = match buildings.iter().find(|x| x.building.eq(unit_b)) {
            Some(v) => v,
            None => continue,
        };

        let (_, info_c) = info_query
            .iter()
            .find(|x| unit_c.iter().any(|y| y.eq(&x.0)))
            .unwrap();

        let (_, desc_c) = desc_query
            .iter()
            .find(|x| info_c.iter().any(|y| y.eq(&x.0)))
            .unwrap();

        let (_, mut text) = text_query
            .iter_mut()
            .find(|x| desc_c.iter().any(|y| y.eq(&x.0)))
            .unwrap();

        let amount = *savegame.buildings.get(unit_b).unwrap_or(&0) as f64;

        let price = match shop_settings.mode {
            ShopMode::Buy => {
                building.price as f64 * ITEM_PRICE_MULTIPLIER.powf(amount + unit_amount)
            }
            ShopMode::Sell => building.price as f64 / 4.0 * ITEM_PRICE_MULTIPLIER.powf(amount),
        }
        .trunc();

        text.sections[0] = price.to_string().into();
    }
}

pub fn set_availability_for_unit_items(
    mut commands: Commands,
    mut query: Query<
        (
            Entity,
            &Building,
            &mut BackgroundColor,
            Option<&UiUnitDisabledComponent>,
        ),
        With<UiUnitComponent>,
    >,
    shop_settings: Res<ShopSettings>,
    savegame: Res<Persistent<PlayerData>>,
    buildings: Res<Buildings>,
) {
    let buildings = &buildings.0;
    let unit_amount = shop_settings.multiplier.as_usize() as f64;

    for (e, b, mut bg, d) in query.iter_mut() {
        let building = match buildings.iter().find(|x| x.building.eq(b)) {
            Some(v) => v,
            None => continue,
        };

        let amount = *savegame.buildings.get(b).unwrap_or(&0) as f64;

        let money = savegame.money.trunc();

        match shop_settings.mode {
            ShopMode::Buy => {
                let price =
                    building.price as f64 * ITEM_PRICE_MULTIPLIER.powf(amount + unit_amount);
                let price = price.trunc();

                if d.is_some() && price <= money {
                    commands.entity(e).remove::<UiUnitDisabledComponent>();
                    *bg = MAIN_COLOR.into();
                }

                if d.is_none() && price > money {
                    commands.entity(e).insert(UiUnitDisabledComponent);
                    *bg = DARKER_MAIN_COLOR.into();
                }
            }
            ShopMode::Sell => {
                if d.is_some() && unit_amount <= amount {
                    commands.entity(e).remove::<UiUnitDisabledComponent>();
                    *bg = MAIN_COLOR.into();
                }

                if d.is_none() && unit_amount > amount {
                    commands.entity(e).insert(UiUnitDisabledComponent);
                    *bg = DARKER_MAIN_COLOR.into();
                }
            }
        }
    }
}

pub fn despawn_shop_ui(mut commands: Commands, query: Query<Entity, With<UiShopComponent>>) {
    for e in query.iter() {
        commands.entity(e).despawn_recursive();
    }
}
