use bevy::prelude::*;
use bevy_persistent::Persistent;
use discord_rich_presence::{
    activity::{Activity, Assets, Timestamps},
    DiscordIpc, DiscordIpcClient,
};

use crate::{game::player::PlayerPettedEvent, persistent::Savegame, AppState};

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
    app_state: Res<State<AppState>>,
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

        let timestamps = Timestamps::new().start(client.1.timestamp());

        let activity = match app_state.get() {
            AppState::Game => Activity::new()
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
            AppState::MinigamesLobby => Activity::new()
                .details("Selects a mini-game...")
                .assets(Assets::new().large_image("maxon")),
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
