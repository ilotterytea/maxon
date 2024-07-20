use bevy::prelude::*;
use serde::Deserialize;

#[derive(Deserialize, Clone)]
pub struct Pet {
    pub id: String,
    pub price: f64,
    pub multiplier: f64,
    pub icon_data: PetIconData,
}

#[derive(Deserialize, Clone)]
pub struct PetIconData {
    pub columns: u32,
    pub rows: u32,
}

#[derive(Resource, Deserialize, TypePath, Clone, Asset)]
pub struct Pets(pub Vec<Pet>);

#[derive(Component)]
pub struct PetIdComponent(pub String);
