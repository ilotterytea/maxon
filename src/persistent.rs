use std::collections::HashMap;

use bevy::prelude::*;
use bevy_persistent::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Resource, Deserialize, Serialize)]
pub struct Savegame {
    pub money: f64,
    pub multiplier: f64,
    pub name: String,
    pub pets: HashMap<String, u32>,
    pub played_time: u32,
}

impl Default for Savegame {
    fn default() -> Self {
        Self {
            money: 0.0,
            multiplier: 0.0,
            name: whoami::username(),
            pets: HashMap::new(),
            played_time: 0,
        }
    }
}

#[derive(Resource, Deserialize, Serialize)]
pub struct Settings {
    pub music: bool,
    pub is_fullscreen: bool,
    pub language: String,
}

impl Default for Settings {
    fn default() -> Self {
        Self {
            music: true,
            is_fullscreen: false,
            language: "en_us".into(),
        }
    }
}

pub fn setup_persistent_resources(mut commands: Commands) {
    let dir = dirs::config_dir()
        .expect("Failed to get a path to the configuration directory")
        .join(crate::constants::APP_DEVELOPER)
        .join(crate::constants::APP_NAME);

    // Loading the savegame
    commands.insert_resource(
        Persistent::<Savegame>::builder()
            .name("Game savegame")
            .format(StorageFormat::Bincode)
            .path(dir.join("savegame.maxon"))
            .default(Savegame::default())
            .build()
            .expect("Failed to initialize savegame"),
    );

    // Loading the settings
    commands.insert_resource(
        Persistent::<Settings>::builder()
            .name("Game settings")
            .format(StorageFormat::JsonPretty)
            .path(dir.join("settings.set"))
            .default(Settings::default())
            .build()
            .expect("Failed to initialize settings"),
    );
}
