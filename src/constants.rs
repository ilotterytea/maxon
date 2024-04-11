use bevy::prelude::Color;

pub const ROOM_LIGHTS: [Color; 2] = [
    Color::rgb(0.85, 0.8, 1.0),  // Living room
    Color::rgb(1.0, 0.85, 0.64), // Basement
];

pub const APP_DEVELOPER: &str = "ilotterytea";
pub const APP_NAME: &str = "MaxonPettingSimulator";

pub const ITEM_PRICE_MULTIPLIER: f64 = 1.15;

pub const CAMERA_TRANSFORMS: [(
    [f32; 3], // position (x,y,z)
    f32,      // y rotation
); 2] = [
    ([4.0, 2.5, 4.0], 20.0), // Living room
    ([1.0, 4.0, 4.8], 40.0), // Bedroom
];

pub const PLAYER_POSITIONS: [[f32; 3]; 2] = [
    [-4.0, 2.2, -4.0], // Living room
    [-6.0, 3.2, 4.5],  // Bedroom
];

pub const PLAYER_SCALES: [[f32; 3]; 2] = [
    [3.0, 3.0, 3.0], // Living room
    [2.0, 2.0, 2.0], // Bedroom
];
