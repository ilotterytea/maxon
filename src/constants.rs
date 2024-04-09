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
); 3] = [
    ([4.0, 2.5, 4.0], 20.0), // Living room
    ([0.0, -8.5, 6.0], 0.0), // Basement
];
