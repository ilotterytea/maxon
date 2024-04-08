use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::{
    constants::ITEM_PRICE_MULTIPLIER,
    game::{
        basement::building::{Building, Buildings},
        PlayerData,
    },
    style::{DARK_MAIN_COLOR, MAIN_COLOR},
};

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

pub fn purchase_or_sell_item(
    mut query: Query<
        (&Building, &Interaction, &mut BackgroundColor),
        (
            With<UiUnitComponent>,
            Without<UiUnitDisabledComponent>,
            Changed<Interaction>,
        ),
    >,
    mut savegame: ResMut<Persistent<PlayerData>>,
    shop_settings: Res<ShopSettings>,
    buildings: Res<Buildings>,
) {
    let buildings = &buildings.0;
    let unit_amount = shop_settings.multiplier.as_usize() as f64;

    for (b, i, mut bg) in query.iter_mut() {
        *bg = match *i {
            Interaction::Hovered => DARK_MAIN_COLOR,
            _ => MAIN_COLOR,
        }
        .into();

        if *i != Interaction::Pressed {
            continue;
        }

        let building = match buildings.iter().find(|x| x.building.eq(b)) {
            Some(v) => v,
            None => continue,
        };

        let amount = *savegame.buildings.get(b).unwrap_or(&0) as f64;

        let price = match shop_settings.mode {
            ShopMode::Buy => {
                building.price as f64 * ITEM_PRICE_MULTIPLIER.powf(amount + unit_amount)
            }
            ShopMode::Sell => building.price as f64 / 4.0 * ITEM_PRICE_MULTIPLIER.powf(amount),
        }
        .trunc();

        match shop_settings.mode {
            ShopMode::Buy => {
                savegame.money -= price;
                savegame
                    .buildings
                    .entry(b.clone())
                    .and_modify(|x| *x += unit_amount as usize)
                    .or_insert(unit_amount as usize);
            }
            ShopMode::Sell => {
                savegame.money += price;
                savegame
                    .buildings
                    .entry(b.clone())
                    .and_modify(|x| *x -= unit_amount as usize);
            }
        }
    }
}
