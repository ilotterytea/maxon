use bevy::prelude::*;

pub const ITEM_BG_INACTIVE_COLOR: Color = Color::rgb(0.25, 0.25, 0.25);
pub const ITEM_BG_ACTIVE_COLOR: Color = Color::rgb(0.35, 0.35, 0.35);
pub const ITEM_BORDER_COLOR: Color = Color::rgb(0.25, 0.25, 0.25);

pub const ITEM_HEADER_INACTIVE_COLOR: Color = Color::rgb(0.4, 0.4, 0.4);
pub const ITEM_HEADER_ACTIVE_COLOR: Color = Color::YELLOW;

pub const ITEM_DESC_INACTIVE_COLOR: Color = Color::rgb(0.4, 0.4, 0.4);
pub const ITEM_DESC_ACTIVE_COLOR: Color = Color::rgb(1.0, 1.0, 1.0);

pub fn get_item_header_text_style(font: Handle<Font>) -> TextStyle {
    TextStyle {
        font,
        font_size: 16.0,
        color: ITEM_HEADER_ACTIVE_COLOR,
    }
}

pub fn get_item_desc_text_style(font: Handle<Font>) -> TextStyle {
    TextStyle {
        font,
        font_size: 14.0,
        color: ITEM_DESC_ACTIVE_COLOR,
    }
}

pub fn get_category_header_text_style(font: Handle<Font>) -> TextStyle {
    TextStyle {
        font,
        font_size: 32.0,
        color: ITEM_HEADER_ACTIVE_COLOR,
    }
}
