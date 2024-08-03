use std::time::SystemTime;

use bevy::prelude::*;
use bevy_mod_picking::prelude::*;
use bevy_persistent::Persistent;
use bevy_sprite3d::{Sprite3d, Sprite3dParams};

use crate::{assets::TextureAtlasAssets, persistent::Savegame, SFXAssets};

use super::components::GameObjectComponent;

#[derive(Event)]
pub struct PlayerPettedEvent;

#[derive(Component)]
pub struct PlayerComponent;

pub(super) fn setup_player(
    mut commands: Commands,
    texture_atlas_assets: Res<TextureAtlasAssets>,
    mut sprite_params: Sprite3dParams,
) {
    commands.spawn((
        Sprite3d {
            image: texture_atlas_assets.player_texture.clone(),
            pixels_per_metre: 48.0,
            alpha_mode: AlphaMode::Opaque,
            transform: Transform::from_xyz(2.0, 1.4, 2.4),
            ..default()
        }
        .bundle_with_atlas(
            &mut sprite_params,
            TextureAtlas::from(texture_atlas_assets.player_layout.clone()),
        ),
        PlayerComponent,
        GameObjectComponent,
        On::<Pointer<Click>>::run(click_on_player),
        Name::new("Player"),
    ));
}

pub(super) fn click_on_player(
    mut commands: Commands,
    mut player_query: Query<&mut TextureAtlas, With<PlayerComponent>>,
    texture_atlas_assets: Res<TextureAtlasAssets>,
    texture_atlas_layouts: Res<Assets<TextureAtlasLayout>>,
    sfx_assets: Res<SFXAssets>,
    mut savegame: ResMut<Persistent<Savegame>>,
    mut player_petted_event_writer: EventWriter<PlayerPettedEvent>,
) {
    if let Ok(mut player_atlas) = player_query.get_single_mut() {
        if let Some(layout) = texture_atlas_layouts.get(&texture_atlas_assets.player_layout) {
            player_atlas.index = (player_atlas.index + 1) % layout.textures.len();
        }
    }

    savegame.money += 1.0;

    commands.spawn(AudioBundle {
        source: sfx_assets.purr.clone(),
        settings: PlaybackSettings {
            mode: bevy::audio::PlaybackMode::Despawn,
            ..default()
        },
    });

    player_petted_event_writer.send(PlayerPettedEvent);
}

#[derive(Resource)]
pub struct PlayTimestamp(pub SystemTime);

pub(super) fn setup_play_timestamp(mut commands: Commands) {
    commands.insert_resource(PlayTimestamp(SystemTime::now()));
}

pub(super) fn set_played_time(
    mut commands: Commands,
    mut savegame: ResMut<Persistent<Savegame>>,
    played_time: Res<PlayTimestamp>,
) {
    savegame.played_time += played_time.0.elapsed().unwrap().as_secs() as u32;
    commands.remove_resource::<PlayTimestamp>();
}
