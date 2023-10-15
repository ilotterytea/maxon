use bevy::prelude::*;

pub struct Item {
    pub id: String,
    pub price: i128,
    pub multiplier: f32,
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
