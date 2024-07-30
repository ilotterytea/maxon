use bevy::{
    audio::PlaybackMode,
    prelude::*,
    window::{PrimaryWindow, WindowMode},
};
use bevy_persistent::Persistent;

use crate::{localization::setup_localization, persistent::Settings, AppState, MusicAssets};

pub struct BootPlugin;

impl Plugin for BootPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            OnEnter(AppState::Boot),
            (setup_localization, apply_settings, create_music_source),
        )
        .add_systems(Update, move_to_menu_screen.run_if(in_state(AppState::Boot)));
    }
}

fn move_to_menu_screen(mut state: ResMut<NextState<AppState>>) {
    state.set(AppState::Menu);
}

fn apply_settings(
    settings: Res<Persistent<Settings>>,
    mut window: Query<&mut Window, With<PrimaryWindow>>,
) {
    let mut window = window.single_mut();

    if settings.is_fullscreen {
        window.mode = WindowMode::BorderlessFullscreen;
    }
}

#[derive(Component)]
pub struct MusicSourceComponent;

fn create_music_source(
    mut commands: Commands,
    music_assets: Res<MusicAssets>,
    settings: Res<Persistent<Settings>>,
    query: Query<&MusicSourceComponent>,
) {
    if !query.is_empty() {
        return;
    }

    commands.spawn((
        AudioBundle {
            source: music_assets.menu.clone(),
            settings: PlaybackSettings {
                mode: PlaybackMode::Loop,
                paused: !settings.music,
                ..default()
            },
        },
        MusicSourceComponent,
        Name::new("Music source"),
    ));
}
