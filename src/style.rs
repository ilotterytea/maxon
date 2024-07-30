use bevy::{color::palettes::css as color, prelude::*};

use crate::assets::FontAssets;

pub const STORE_BG_COLOR: Srgba = Srgba::new(89.0 / 255.0, 65.0 / 255.0, 58.0 / 255.0, 1.0);
pub const STORE_CONTROL_BG_COLOR: Srgba = Srgba::new(71.0 / 255.0, 49.0 / 255.0, 44.0 / 255.0, 1.0);
pub const STORE_LIST_BG_COLOR: Srgba = Srgba::new(48.0 / 255.0, 34.0 / 255.0, 30.0 / 255.0, 1.0);
pub const STORE_ITEM_BG_COLOR: Srgba = Srgba::new(64.0 / 255.0, 45.0 / 255.0, 40.0 / 255.0, 1.0);
pub const STORE_ITEM_HOVER_BG_COLOR: Srgba =
    Srgba::new(75.0 / 255.0, 53.0 / 255.0, 47.0 / 255.0, 1.0);
pub const STORE_ITEM_DISABLED_BG_COLOR: Srgba =
    Srgba::new(48.0 / 255.0, 34.0 / 255.0, 30.0 / 255.0, 1.0);

pub fn get_text_style_header(font_assets: &Res<FontAssets>) -> TextStyle {
    TextStyle {
        font: font_assets.text.clone(),
        font_size: 32.0,
        color: color::WHITE.into(),
    }
}

pub fn get_text_style_default(font_assets: &Res<FontAssets>) -> TextStyle {
    TextStyle {
        font: font_assets.text.clone(),
        font_size: 24.0,
        color: color::WHITE.into(),
    }
}

pub fn get_text_style_pet_amount(font_assets: &Res<FontAssets>) -> TextStyle {
    TextStyle {
        font: font_assets.text.clone(),
        font_size: 64.0,
        color: Srgba::new(38.0 / 255.0, 28.0 / 255.0, 17.0 / 255.0, 1.0).into(),
    }
}

pub fn get_text_style_debug(font_assets: &Res<FontAssets>) -> TextStyle {
    TextStyle {
        font: font_assets.debug.clone(),
        font_size: 16.0,
        color: color::GRAY.into(),
    }
}

pub fn get_text_style_debug_value(font_assets: &Res<FontAssets>) -> TextStyle {
    TextStyle {
        font: font_assets.debug.clone(),
        font_size: 16.0,
        color: color::LIME.into(),
    }
}
