use bevy::prelude::*;
use bevy_sprite3d::{Sprite3d, Sprite3dParams};

use crate::assets::TextureAtlasAssets;

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
        Name::new("Player"),
    ));
}
