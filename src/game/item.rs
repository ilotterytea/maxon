use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::{
    assets::AppAssets,
    constants::ITEM_PRICE_MULTIPLIER,
    game::building::{Building, BuildingField},
    style::{
        get_item_desc_text_style, ITEM_BG_ACTIVE_COLOR, ITEM_BG_INACTIVE_COLOR,
        ITEM_DESC_ACTIVE_COLOR, ITEM_DESC_INACTIVE_COLOR, ITEM_HEADER_ACTIVE_COLOR,
        ITEM_HEADER_INACTIVE_COLOR,
    },
};

use super::{
    player::PlayerData,
    ui::{
        UiInventory, UiTextItemCostComponent, UiTextItemHeaderComponent, UiTextItemIconComponent,
        UiTextMoneyComponent,
    },
};

pub struct Item {
    pub id: String,
    pub price: f64,
    pub multiplier: f64,
}

#[derive(Resource)]
pub struct Items(pub Vec<Item>);

#[derive(Component)]
pub struct ItemComponent(pub String);

pub fn initialize_items(mut commands: Commands) {
    let items: Vec<Item> = vec![
        Item {
            id: "Building.Bedroom".to_string(),
            price: 10.0,
            multiplier: 0.1,
        },
        Item {
            id: "Building.Kitchen".to_string(),
            price: 100.0,
            multiplier: 1.0,
        },
        Item {
            id: "Building.Canyon".to_string(),
            price: 1200.0,
            multiplier: 8.0,
        },
        Item {
            id: "Building.Sea".to_string(),
            price: 13000.0,
            multiplier: 48.0,
        },
    ];

    commands.insert_resource(Items(items));
}

pub fn check_item_for_purchase(
    mut set: ParamSet<(
        Query<(&mut BackgroundColor, &ItemComponent), With<ItemComponent>>,
        Query<(&mut Text, &UiTextItemHeaderComponent), With<UiTextItemHeaderComponent>>,
        Query<(&mut Text, &UiTextItemCostComponent), With<UiTextItemCostComponent>>,
    )>,
    player_data: Res<Persistent<PlayerData>>,
    items: Res<Items>,
) {
    for (mut bg, c) in set.p0().iter_mut() {
        if let Some(item) = items.0.iter().find(|x| x.id.eq(&c.0)) {
            if item.price > player_data.money {
                *bg = ITEM_BG_INACTIVE_COLOR.into();
            } else {
                *bg = ITEM_BG_ACTIVE_COLOR.into();
            }
        }
    }

    for (mut text, c) in set.p1().iter_mut() {
        if let Some(item) = items.0.iter().find(|x| x.id.eq(&c.0)) {
            if item.price > player_data.money {
                text.sections[0].style.color = ITEM_HEADER_INACTIVE_COLOR;
            } else {
                text.sections[0].style.color = ITEM_HEADER_ACTIVE_COLOR;
            }
        }
    }

    for (mut text, c) in set.p2().iter_mut() {
        if let Some(item) = items.0.iter().find(|x| x.id.eq(&c.0)) {
            if item.price > player_data.money {
                text.sections[0].style.color = ITEM_DESC_INACTIVE_COLOR;
            } else {
                text.sections[0].style.color = ITEM_DESC_ACTIVE_COLOR;
            }
        }
    }
}

pub fn purchase_item(
    mut player_data: ResMut<Persistent<PlayerData>>,
    mut items: ResMut<Items>,
    button_query: Query<
        (&Interaction, &ItemComponent),
        (Changed<Interaction>, With<ItemComponent>),
    >,
    mut item_cost_query: Query<
        (&mut Text, &UiTextItemCostComponent),
        With<UiTextItemCostComponent>,
    >,
    mut commands: Commands,
    inv_query: Query<Entity, With<UiInventory>>,
    app_assets: Res<AppAssets>,
) {
    if let Ok(e) = inv_query.get_single() {
        for (i, c) in button_query.iter() {
            match *i {
                Interaction::Pressed => {
                    println!("click! {}", c.0);

                    if let Some(item) = items.0.iter_mut().find(|x| x.id.eq(&c.0)) {
                        if player_data.money < item.price {
                            continue;
                        }

                        let amount = if let Some(v) = player_data.purchased_items.get(&c.0) {
                            v + 1
                        } else {
                            1
                        };

                        player_data
                            .update(|data| {
                                data.money -= item.price;
                                data.multiplier += item.multiplier;
                                data.purchased_items.insert(c.0.clone(), amount);
                            })
                            .expect("Failed to update player data");

                        let init_price = item.price as f64 / ITEM_PRICE_MULTIPLIER.powi(amount - 1);
                        item.price = (init_price * ITEM_PRICE_MULTIPLIER.powi(amount)).round();

                        if let Some((mut text, _)) =
                            item_cost_query.iter_mut().find(|x| x.1 .0.eq(&c.0))
                        {
                            text.sections[0].value = item.price.to_string();
                        }

                        // Create a new building in inventory
                        if amount == 1 {
                            if let Ok(building) = Building::from_str(c.0.as_str()) {
                                let handles = building.get_image_handles(&app_assets);

                                let eid = commands
                                    .spawn((
                                        NodeBundle {
                                            style: Style {
                                                display: Display::Flex,
                                                flex_direction: FlexDirection::Column,
                                                width: Val::Percent(100.0),
                                                height: Val::Percent(15.0),
                                                border: UiRect::all(Val::Px(2.0)),
                                                ..default()
                                            },
                                            background_color: Color::GRAY.into(),
                                            border_color: Color::DARK_GRAY.into(),
                                            ..default()
                                        },
                                        building.clone(),
                                    ))
                                    // Control panel
                                    .with_children(|parent| {
                                        parent.spawn(
                                            TextBundle::from_section(
                                                "kek",
                                                get_item_desc_text_style(
                                                    app_assets.font_text.clone(),
                                                ),
                                            )
                                            .with_style(Style {
                                                width: Val::Percent(100.0),
                                                flex_grow: 1.0,
                                                ..default()
                                            }),
                                        );
                                    })
                                    // Field
                                    .with_children(|parent| {
                                        parent.spawn((
                                            ImageBundle {
                                                image: UiImage::new(handles.0),
                                                style: Style {
                                                    width: Val::Percent(100.0),
                                                    height: Val::Percent(100.0),
                                                    flex_grow: 2.0,
                                                    ..default()
                                                },
                                                ..default()
                                            },
                                            BuildingField(building),
                                        ));
                                    })
                                    .id();

                                commands.entity(e).add_child(eid);
                            }
                        }
                    }
                }
                _ => {}
            }
        }
    }
}
