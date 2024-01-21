use bevy::prelude::Color;

pub const LIGHT_ROOM: Color = Color::rgb(0.85, 0.8, 1.0);

pub const APP_DEVELOPER: &str = "ilotterytea";
pub const APP_NAME: &str = "MaxonPettingSimulator";

pub const ITEM_PRICE_MULTIPLIER: f64 = 1.15;

pub const CAMERA_TRANSFORMS: [(
    [f32; 3], // position (x,y,z)
    [f32; 4], // rotation (x,y,z,angle
); 1] = [
    ([4.0, 2.5, 4.0], [0.0, 1.0, 0.0, 0.784]), // Living room
];
