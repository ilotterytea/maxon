use bevy::prelude::*;

use crate::assets::AppAssets;

#[derive(Component)]
pub struct Player;

pub fn generate_player(mut commands: Commands, app_assets: Res<AppAssets>) {
    commands.spawn((
        SpriteSheetBundle {
            sprite: TextureAtlasSprite::new(0),
            texture_atlas: app_assets.cat_maxon.clone(),
            ..default()
        },
        Player,
    ));
}

pub fn click_on_player(
    mouse_input: Res<Input<MouseButton>>,
    window: Query<&Window>,
    mut player: Query<&mut TextureAtlasSprite, With<Player>>,
    app_assets: Res<AppAssets>,
    assets: Res<Assets<TextureAtlas>>,
) {
    if let Ok(window) = window.get_single() {
        if let Some(cursor_position) = window.cursor_position() {
            if let Ok(mut s) = player.get_single_mut() {
                let sprite = assets.get(&app_assets.cat_maxon).unwrap();
                let rect = sprite.textures.get(s.index).unwrap();

                let x0 = window.width() / 2.0 - rect.width() / 2.0;
                let x1 = x0 + rect.width();

                let y0 = window.height() / 2.0 - rect.height() / 2.0;
                let y1 = y0 + rect.height();

                if cursor_position.x < x0
                    || x1 < cursor_position.x
                    || cursor_position.y < y0
                    || y1 < cursor_position.y
                    || !mouse_input.just_pressed(MouseButton::Left)
                {
                    return;
                }

                s.index += 1;

                if s.index >= sprite.textures.len() {
                    s.index = 0;
                }
            }
        }
    }
}
