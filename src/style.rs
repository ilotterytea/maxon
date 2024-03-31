use bevy::prelude::*;

pub const CONTROLPANEL_BG_COLOR: Color = Color::rgba(0.0, 0.0, 0.0, 0.5);

pub const MAIN_COLOR: Color = Color::rgb(1.0, 169.0 / 255.0, 44.0 / 255.0);
pub const DARK_MAIN_COLOR: Color = Color::rgb(216.0 / 255.0, 152.0 / 255.0, 57.0 / 255.0);
pub const DARKER_MAIN_COLOR: Color = Color::rgb(132.0 / 255.0, 100.0 / 255.0, 54.0 / 255.0);

pub const BUILDING_HEADER_COLOR: Color = Color::rgb(255.0 / 255.0, 245.0 / 255.0, 160.0 / 255.0);

pub fn get_building_header_text_style(font: Handle<Font>) -> TextStyle {
    TextStyle {
        font,
        font_size: 32.0,
        color: BUILDING_HEADER_COLOR,
    }
}

pub fn get_savegame_ui_text_style(font: Handle<Font>) -> TextStyle {
    TextStyle {
        font,
        font_size: 48.0,
        color: MAIN_COLOR,
    }
}
