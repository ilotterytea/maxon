use std::collections::HashMap;

use bevy::prelude::*;
use bevy_persistent::{Persistent, StorageFormat};
use serde::{Deserialize, Serialize};

use crate::{
    assets::AppAssets,
    constants::{APP_DEVELOPER, APP_NAME},
};

#[derive(Resource, Serialize, Deserialize)]
pub struct PlayerData {
    pub money: f64,
    pub multiplier: f64,
    pub purchased_items: HashMap<String, i32>,
}

impl Default for PlayerData {
    fn default() -> Self {
        Self {
            money: 0.0,
            multiplier: 1.0,
            purchased_items: HashMap::new(),
        }
    }
}

pub fn init_player_data(mut commands: Commands) {
    let path = dirs::data_dir().unwrap().join(APP_DEVELOPER).join(APP_NAME);

    commands.insert_resource(
        Persistent::<PlayerData>::builder()
            .name("PlayerData")
            .format(StorageFormat::Json)
            .path(path.join("savegame.maxon"))
            .default(PlayerData::default())
            .build()
            .expect("Failed to build player data"),
    );
}

#[derive(Component)]
pub struct PlayerComponent;

pub fn generate_player(mut commands: Commands, app_assets: Res<AppAssets>) {
    commands.spawn((
        SpriteSheetBundle {
            sprite: TextureAtlasSprite::new(0),
            texture_atlas: app_assets.cat_maxon.clone(),
            ..default()
        },
        PlayerComponent,
    ));
}

pub fn click_on_player(
    mouse_input: Res<Input<MouseButton>>,
    window: Query<&Window>,
    mut player_sprite: Query<&mut TextureAtlasSprite, With<PlayerComponent>>,
    mut player_data: ResMut<Persistent<PlayerData>>,
    app_assets: Res<AppAssets>,
    assets: Res<Assets<TextureAtlas>>,
) {
    if let Ok(window) = window.get_single() {
        if let Some(cursor_position) = window.cursor_position() {
            if let Ok(mut s) = player_sprite.get_single_mut() {
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

                player_data
                    .update(|data| {
                        data.money += 1.0;
                    })
                    .expect("Failed to update player data");
            }
        }
    }
}
