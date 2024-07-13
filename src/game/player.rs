use bevy::prelude::*;
use bevy_mod_picking::prelude::*;
use bevy_persistent::Persistent;
use bevy_sprite3d::{Sprite3d, Sprite3dParams};

use crate::{assets::TextureAtlasAssets, persistent::Savegame};

use super::components::GameObjectComponent;

#[derive(Component)]
pub struct PlayerComponent;

pub fn setup_player(
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

pub fn click_on_player(
    mut player_query: Query<&mut TextureAtlas, With<PlayerComponent>>,
    texture_atlas_assets: Res<TextureAtlasAssets>,
    texture_atlas_layouts: Res<Assets<TextureAtlasLayout>>,
    mut savegame: ResMut<Persistent<Savegame>>,
) {
    if let Ok(mut player_atlas) = player_query.get_single_mut() {
        if let Some(layout) = texture_atlas_layouts.get(&texture_atlas_assets.player_layout) {
            player_atlas.index = (player_atlas.index + 1) % layout.textures.len();
        }
    }

    savegame.money += 1.0;
}
