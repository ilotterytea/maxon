use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::{persistent::Savegame, SFXAssets};

use super::ui::PetDisabledComponent;

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

pub fn play_sound_for_disabled_pets(
    mut commands: Commands,
    query: Query<&Interaction, (With<PetDisabledComponent>, Changed<Interaction>)>,
    sfx_assets: Res<SFXAssets>,
) {
    for i in query.iter() {
        if *i == Interaction::Pressed {
            commands.spawn(AudioBundle {
                source: sfx_assets.not_enough_money.clone(),
                settings: PlaybackSettings {
                    mode: bevy::audio::PlaybackMode::Despawn,
                    ..default()
                },
            });
        }
    }
}
