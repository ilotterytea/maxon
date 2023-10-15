use bevy::prelude::*;

use super::player::PlayerData;

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
    mut button_query: Query<(&mut BackgroundColor, &ItemComponent), With<ItemComponent>>,
    player_data: Res<PlayerData>,
    items: Res<Items>,
) {
    for (mut bg, c) in button_query.iter_mut() {
        if let Some(item) = items.0.iter().find(|x| x.id.eq(&c.0)) {
            if item.price > player_data.money {
                *bg = Color::DARK_GRAY.into();
            } else {
                *bg = Color::ORANGE.into();
            }
        }
    }
}

pub fn purchase_item(
    button_query: Query<
        (&Interaction, &ItemComponent),
        (Changed<Interaction>, With<ItemComponent>),
    >,
) {
    for (i, c) in button_query.iter() {
        match *i {
            Interaction::Pressed => println!("click! {}", c.0),
            _ => {}
        }
    }
}
