use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::persistent::Savegame;

#[derive(Resource)]
pub struct MultiplierTickTimer(pub Timer);

pub fn setup_multiplier_tick(mut commands: Commands) {
    commands.insert_resource(MultiplierTickTimer(Timer::from_seconds(
        0.1,
        TimerMode::Repeating,
    )));
}

pub fn tick_multiplier(
    time: Res<Time>,
    mut timer: ResMut<MultiplierTickTimer>,
    mut savegame: ResMut<Persistent<Savegame>>,
) {
    if savegame.multiplier == 0.0 {
        return;
    }

    timer.0.tick(time.delta());

    if timer.0.just_finished() {
        savegame.money += savegame.multiplier / 10.0;
    }
}

#[derive(Event)]
pub struct PurchaseEvent;
