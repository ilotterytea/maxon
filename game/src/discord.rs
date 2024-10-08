use bevy::prelude::*;
use bevy_persistent::Persistent;
use discord_rich_presence::{
    activity::{Activity, Assets, Timestamps},
    DiscordIpc, DiscordIpcClient,
};

use crate::{
    game::{
        minigames::{runner::systems::ScoreResource, slots::SlotsResource, MinigameState},
        player::PlayerPettedEvent,
    },
    persistent::Savegame,
    AppState,
};

#[derive(Resource)]
pub struct DiscordIpcResource(pub DiscordIpcClient, pub chrono::DateTime<chrono::Utc>);

#[derive(Resource)]
pub struct DiscordIpcTimer(pub Timer);

pub fn init_discord_ipc_client(mut commands: Commands) {
    let mut client = match DiscordIpcClient::new(include_str!("../config/discord_game_id")) {
        Ok(c) => c,
        Err(e) => {
            warn!("Failed to create Discord IPC client: {}", e);
            return;
        }
    };

    if let Some(e) = client.connect().err() {
        warn!("Failed to connect Discord IPC client: {}", e);
        return;
    }

    if let Some(e) = client
        .set_activity(
            Activity::new()
                .details("Petting Maxon")
                .assets(Assets::new().large_image("maxon")),
        )
        .err()
    {
        warn!("Failed to set activity: {}", e);
        return;
    }

    commands.insert_resource(DiscordIpcResource(client, chrono::Utc::now()));

    commands.insert_resource(DiscordIpcTimer(Timer::from_seconds(
        2.0,
        TimerMode::Repeating,
    )));
}

pub fn update_discord_ipc_client(
    client: Option<ResMut<DiscordIpcResource>>,
    timer: Option<ResMut<DiscordIpcTimer>>,
    score: Option<Res<ScoreResource>>,
    slots: Option<Res<SlotsResource>>,
    app_state: Res<State<AppState>>,
    minigame_state: Res<State<MinigameState>>,
    savegame: Res<Persistent<Savegame>>,
    time: Res<Time>,
    mut player_petted_event_reader: EventReader<PlayerPettedEvent>,
) {
    if timer.is_none() || client.is_none() {
        return;
    }

    let mut timer = timer.unwrap();
    let mut client = client.unwrap();

    timer.0.tick(time.delta());

    if timer.0.just_finished() {
        let was_player_petted = player_petted_event_reader.read().next().is_some();

        let savegame_showcase = format!(
            "{}💵 - {}ⅹ - {} 🐱",
            savegame.money.trunc(),
            savegame.multiplier.trunc(),
            savegame.pets.values().sum::<u32>()
        );

        let score = match minigame_state.get() {
            MinigameState::Runner => {
                format!(
                    "{} PTS",
                    if let Some(p) = score {
                        p.0.to_string()
                    } else {
                        "???".into()
                    }
                )
            }
            MinigameState::Slots => {
                format!(
                    "{}{}/{}",
                    if let Some(r) = slots {
                        format!(
                            "{} ({}%) 💵 bet - ",
                            r.stake.trunc(),
                            r.stake_percent.trunc()
                        )
                    } else {
                        "".into()
                    },
                    savegame.minigames.slots.total_spins,
                    savegame.minigames.slots.wins
                )
            }
            MinigameState::None => "".into(),
        };

        let timestamps = Timestamps::new().start(client.1.timestamp());

        let activity = match (app_state.get(), minigame_state.get()) {
            (AppState::Game, _) => Activity::new()
                .details(if was_player_petted {
                    "Petting Maxon"
                } else {
                    "Idle"
                })
                .assets(
                    Assets::new()
                        .large_image("maxon")
                        .large_text(&savegame_showcase),
                ),
            (AppState::MinigamesLobby, _) => Activity::new()
                .details("Selects a mini-game...")
                .assets(Assets::new().large_image("maxon")),
            (AppState::Minigame, MinigameState::Runner) => Activity::new()
                .details("Running through the fields...")
                .assets(
                    Assets::new()
                        .large_image("runner")
                        .large_text(&score)
                        .small_image("maxon")
                        .small_text(&savegame_showcase),
                ),
            (AppState::Minigame, MinigameState::Slots) => {
                Activity::new().details("Spins the slots").assets(
                    Assets::new()
                        .large_image("slots")
                        .large_text(&score)
                        .small_image("maxon")
                        .small_text(&savegame_showcase),
                )
            }
            _ => Activity::new()
                .details("Sitting in Main Menu")
                .assets(Assets::new().large_image("maxon")),
        }
        .timestamps(timestamps);

        if let Some(e) = client.0.set_activity(activity).err() {
            warn!("Failed to set activity: {}", e);
            return;
        }
    }
}

pub fn shutdown_discord_ipc_client(client: Option<ResMut<DiscordIpcResource>>) {
    if client.is_none() {
        return;
    }

    let mut client = client.unwrap();

    if let Some(e) = client.0.close().err() {
        warn!("Failed to close Discord IPC client: {}", e);
    }
}
