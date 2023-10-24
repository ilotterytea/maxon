use bevy::prelude::*;

use crate::style::{
    ITEM_BG_ACTIVE_COLOR, ITEM_BG_INACTIVE_COLOR, ITEM_DESC_ACTIVE_COLOR, ITEM_DESC_INACTIVE_COLOR,
    ITEM_HEADER_ACTIVE_COLOR, ITEM_HEADER_INACTIVE_COLOR,
};

use super::{
    player::PlayerData,
    ui::{
        UiTextItemCostComponent, UiTextItemHeaderComponent, UiTextItemIconComponent,
        UiTextMoneyComponent,
    },
};

pub struct Item {
    pub id: String,
    pub price: i128,
    pub init_price: i128,
    pub multiplier: i128,
}

#[derive(Resource)]
pub struct Items(pub Vec<Item>);

#[derive(Component)]
pub struct ItemComponent(pub String);

pub fn initialize_items(mut commands: Commands) {
    let items: Vec<Item> = vec![Item {
        id: "bror".to_string(),
        price: 10,
        multiplier: 0.1,
    }];

    commands.insert_resource(Items(items));
}

pub fn check_item_for_purchase(
    mut set: ParamSet<(
        Query<(&mut BackgroundColor, &ItemComponent), With<ItemComponent>>,
        Query<(&mut Text, &UiTextItemHeaderComponent), With<UiTextItemHeaderComponent>>,
        Query<(&mut Text, &UiTextItemCostComponent), With<UiTextItemCostComponent>>,
    )>,
    player_data: Res<PlayerData>,
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
    mut player_data: ResMut<PlayerData>,
    mut items: ResMut<Items>,
    button_query: Query<
        (&Interaction, &ItemComponent),
        (Changed<Interaction>, With<ItemComponent>),
    >,
    mut item_cost_query: Query<
        (&mut Text, &UiTextItemCostComponent),
        With<UiTextItemCostComponent>,
    >,
) {
    for (i, c) in button_query.iter() {
        match *i {
            Interaction::Pressed => {
                println!("click! {}", c.0);

                if let Some(item) = items.0.iter_mut().find(|x| x.id.eq(&c.0)) {
                    if player_data.money < item.price {
                        continue;
                    }

                    player_data.money -= item.price;
                    player_data.multiplier += item.multiplier;

                    let amount = if let Some(v) = player_data.purchased_items.get(&c.0) {
                        v + 1
                    } else {
                        1
                    };

                    player_data.purchased_items.insert(c.0.clone(), amount);

                    item.price = (item.init_price as f32 * 1.15_f32.powi(amount)).round() as i128;

                    if let Some((mut text, _)) =
                        item_cost_query.iter_mut().find(|x| x.1 .0.eq(&c.0))
                    {
                        text.sections[0].value = item.price.to_string();
                    }
                }
            }
            _ => {}
        }
    }
}
