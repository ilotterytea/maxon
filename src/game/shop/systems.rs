use bevy::prelude::*;

use super::*;

pub fn set_shop_mode(
    mut query: Query<(&ShopMode, &Interaction, &mut BackgroundColor), With<ShopMode>>,
    mut shop_settings: ResMut<ShopSettings>,
) {
    for (m, i, mut bg) in query.iter_mut() {
        *bg = match (m, *i, shop_settings.mode.eq(m)) {
            (ShopMode::Buy, Interaction::Hovered, _) => Color::YELLOW_GREEN,
            (ShopMode::Buy, _, true) => Color::LIME_GREEN,
            (ShopMode::Buy, _, false) => Color::DARK_GREEN,
            (ShopMode::Sell, Interaction::Hovered, _) => Color::ORANGE_RED,
            (ShopMode::Sell, _, true) => Color::CRIMSON,
            (ShopMode::Sell, _, false) => Color::MAROON,
        }
        .into();

        if *i != Interaction::Pressed {
            continue;
        }

        shop_settings.mode = m.clone();
    }
}

pub fn set_shop_multiplier(
    mut query: Query<(&ShopMultiplier, &Interaction, &mut BackgroundColor), With<ShopMultiplier>>,
    mut shop_settings: ResMut<ShopSettings>,
) {
    for (m, i, mut bg) in query.iter_mut() {
        *bg = match (*i, shop_settings.multiplier.eq(m)) {
            (Interaction::Hovered, _) => Color::SILVER,
            (_, true) => Color::GRAY,
            (_, false) => Color::DARK_GRAY,
        }
        .into();

        if *i != Interaction::Pressed {
            continue;
        }

        shop_settings.multiplier = m.clone();
    }
}
