use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::{
    constants::ITEM_PRICE_MULTIPLIER,
    game::{
        basement::building::{Building, Buildings},
        PlayerData,
    },
};

use super::ui::{ControlButtonComponent, MultiplierButtonComponent};

#[derive(Component)]
pub struct ControlButtonDisabledComponent;

pub fn set_availability_for_control_buttons(
    mut commands: Commands,
    mut query: Query<
        (
            Entity,
            &MultiplierButtonComponent,
            &ControlButtonComponent,
            &Building,
            &mut BackgroundColor,
            Option<&ControlButtonDisabledComponent>,
        ),
        (
            With<Building>,
            With<ControlButtonComponent>,
            With<MultiplierButtonComponent>,
        ),
    >,
    savegame: Res<Persistent<PlayerData>>,
    buildings: Res<Buildings>,
) {
    let buildings = &buildings.0;

    for (e, m, c, bu, mut b, cb) in query.iter_mut() {
        if let Some(bd) = buildings.iter().find(|x| x.building.eq(bu)) {
            let amount = savegame.buildings.get(bu).unwrap_or(&0);

            match *c {
                ControlButtonComponent::Buy => {
                    let price = bd.price as f64 * ITEM_PRICE_MULTIPLIER.powf(*amount as f64);
                    let price = price.trunc();

                    if cb.is_none() && price > savegame.money {
                        commands.entity(e).insert(ControlButtonDisabledComponent);
                        *b = Color::DARK_GRAY.into();
                    }

                    if cb.is_some() && price <= savegame.money {
                        commands
                            .entity(e)
                            .remove::<ControlButtonDisabledComponent>();
                        *b = Color::GRAY.into();
                    }
                }
                ControlButtonComponent::Sell => {
                    let sell_amount = m.as_usize();

                    if sell_amount > *amount {
                        commands.entity(e).insert(ControlButtonDisabledComponent);
                        *b = Color::DARK_GRAY.into();
                    } else {
                        commands
                            .entity(e)
                            .remove::<ControlButtonDisabledComponent>();
                        *b = Color::GRAY.into();
                    }
                }
            }
        }
    }
}
