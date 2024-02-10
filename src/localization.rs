use bevy::{
    prelude::*,
    reflect::{TypePath, TypeUuid},
    utils::HashMap,
};
use bevy_persistent::Persistent;
use serde::{
    de::{Error, Visitor},
    Deserialize,
};

use crate::{assets::AppAssets, settings::Settings};

#[derive(PartialEq, Eq, Hash, Clone)]
pub enum LineId {
    DebugVersion,
    DebugCfps,
    DebugCmem,

    SplashDisclaimer,

    MenuLastsavegameFound,
    MenuLastsavegameEmpty,

    MenuPressstart,
    MenuPlaygame,
    MenuOptions,
    MenuQuit,
    MenuContinue,

    SavegameTitle,
    SavegameNew,
    SavegamePoints,
    SavegameMultiplier,
    SavegameMultiplierCount,
    SavegamePurchased,
    SavegameYourname,

    OptionsTitle,
    OptionsMusic,
    OptionsSound,
    OptionsVsync,
    OptionsFullscreen,
    OptionsLanguage,
    OptionsDebug,
    OptionsReset,
    OptionsClose,
    OptionsGeneral,
    OptionsVideo,
    OptionsAudio,

    GamePetshop,
    GamePoints,
    GameMultiplier,
    GameNewpoint,

    DialogsNotenoughpoints,

    GiftsEmpty,
    GiftsMultiplier,
    GiftsPoints,
    GiftsNewpet,

    GameInventoryTitle,

    PetBrorName,
    PetBrorDesc,

    PetSandwichName,
    PetSandwichDesc,

    PetManlooshkaName,
    PetManlooshkaDesc,

    PetThirstyName,
    PetThirstyDesc,

    PetFuriosName,
    PetFuriosDesc,

    PetTvcatName,
    PetTvcatDesc,

    PetProgcatName,
    PetProgcatDesc,

    PetScreamcatName,
    PetScreamcatDesc,

    PetHellcatName,
    PetHellcatDesc,

    PetLurkerName,
    PetLurkerDesc,

    PetPianoName,
    PetPianoDesc,

    PetBeeName,
    PetBeeDesc,

    PetBusyName,
    PetBusyDesc,

    PetAeaeName,
    PetAeaeDesc,

    PetSuccatName,
    PetSuccatDesc,
}

impl LineId {
    pub fn from_str(value: &str) -> Option<LineId> {
        let parts: Vec<&str> = value.split('.').collect();

        match parts.as_slice() {
            ["debug", "version"] => Some(LineId::DebugVersion),
            ["debug", "c_fps"] => Some(LineId::DebugCfps),
            ["debug", "c_mem"] => Some(LineId::DebugCmem),
            ["splash", "disclaimer"] => Some(LineId::SplashDisclaimer),
            ["menu", "last_savegame", "found"] => Some(LineId::MenuLastsavegameFound),
            ["menu", "last_savegame", "empty"] => Some(LineId::MenuLastsavegameEmpty),
            ["menu", "pressStart"] => Some(LineId::MenuPressstart),
            ["menu", "playGame"] => Some(LineId::MenuPlaygame),
            ["menu", "options"] => Some(LineId::MenuOptions),
            ["menu", "quit"] => Some(LineId::MenuQuit),
            ["menu", "continue"] => Some(LineId::MenuContinue),
            ["savegame", "title"] => Some(LineId::SavegameTitle),
            ["savegame", "new"] => Some(LineId::SavegameNew),
            ["savegame", "points"] => Some(LineId::SavegamePoints),
            ["savegame", "multiplier"] => Some(LineId::SavegameMultiplier),
            ["savegame", "multiplier", "count"] => Some(LineId::SavegameMultiplierCount),
            ["savegame", "purchased"] => Some(LineId::SavegamePurchased),
            ["savegame", "your_name"] => Some(LineId::SavegameYourname),
            ["options", "title"] => Some(LineId::OptionsTitle),
            ["options", "music"] => Some(LineId::OptionsMusic),
            ["options", "sound"] => Some(LineId::OptionsSound),
            ["options", "vsync"] => Some(LineId::OptionsVsync),
            ["options", "fullscreen"] => Some(LineId::OptionsFullscreen),
            ["options", "language"] => Some(LineId::OptionsLanguage),
            ["options", "debug"] => Some(LineId::OptionsDebug),
            ["options", "reset"] => Some(LineId::OptionsReset),
            ["options", "close"] => Some(LineId::OptionsClose),
            ["options", "general"] => Some(LineId::OptionsGeneral),
            ["options", "video"] => Some(LineId::OptionsVideo),
            ["options", "audio"] => Some(LineId::OptionsAudio),
            ["game", "petShop"] => Some(LineId::GamePetshop),
            ["game", "points"] => Some(LineId::GamePoints),
            ["game", "multiplier"] => Some(LineId::GameMultiplier),
            ["game", "newPoint"] => Some(LineId::GameNewpoint),
            ["dialogs", "not_enough_points"] => Some(LineId::DialogsNotenoughpoints),
            ["gifts", "empty"] => Some(LineId::GiftsEmpty),
            ["gifts", "multiplier"] => Some(LineId::GiftsMultiplier),
            ["gifts", "points"] => Some(LineId::GiftsPoints),
            ["gifts", "new_pet"] => Some(LineId::GiftsNewpet),
            ["game", "inventory", "title"] => Some(LineId::GameInventoryTitle),
            ["pet", "bror", "name"] => Some(LineId::PetBrorName),
            ["pet", "bror", "desc"] => Some(LineId::PetBrorDesc),
            ["pet", "sandwich", "name"] => Some(LineId::PetSandwichName),
            ["pet", "sandwich", "desc"] => Some(LineId::PetSandwichDesc),
            ["pet", "manlooshka", "name"] => Some(LineId::PetManlooshkaName),
            ["pet", "manlooshka", "desc"] => Some(LineId::PetManlooshkaDesc),
            ["pet", "thirsty", "name"] => Some(LineId::PetThirstyName),
            ["pet", "thirsty", "desc"] => Some(LineId::PetThirstyDesc),
            ["pet", "furios", "name"] => Some(LineId::PetFuriosName),
            ["pet", "furios", "desc"] => Some(LineId::PetFuriosDesc),
            ["pet", "tvcat", "name"] => Some(LineId::PetTvcatName),
            ["pet", "tvcat", "desc"] => Some(LineId::PetTvcatDesc),
            ["pet", "progcat", "name"] => Some(LineId::PetProgcatName),
            ["pet", "progcat", "desc"] => Some(LineId::PetProgcatDesc),
            ["pet", "screamcat", "name"] => Some(LineId::PetScreamcatName),
            ["pet", "screamcat", "desc"] => Some(LineId::PetScreamcatDesc),
            ["pet", "hellcat", "name"] => Some(LineId::PetHellcatName),
            ["pet", "hellcat", "desc"] => Some(LineId::PetHellcatDesc),
            ["pet", "lurker", "name"] => Some(LineId::PetLurkerName),
            ["pet", "lurker", "desc"] => Some(LineId::PetLurkerDesc),
            ["pet", "piano", "name"] => Some(LineId::PetPianoName),
            ["pet", "piano", "desc"] => Some(LineId::PetPianoDesc),
            ["pet", "bee", "name"] => Some(LineId::PetBeeName),
            ["pet", "bee", "desc"] => Some(LineId::PetBeeDesc),
            ["pet", "busy", "name"] => Some(LineId::PetBusyName),
            ["pet", "busy", "desc"] => Some(LineId::PetBusyDesc),
            ["pet", "aeae", "name"] => Some(LineId::PetAeaeName),
            ["pet", "aeae", "desc"] => Some(LineId::PetAeaeDesc),
            ["pet", "succat", "name"] => Some(LineId::PetSuccatName),
            ["pet", "succat", "desc"] => Some(LineId::PetSuccatDesc),
            _ => None,
        }
    }
}

impl<'de> Deserialize<'de> for LineId {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        struct CustomVisitor;

        impl<'de> Visitor<'de> for CustomVisitor {
            type Value = LineId;

            fn expecting(&self, formatter: &mut std::fmt::Formatter) -> std::fmt::Result {
                formatter.write_str("a string representing LineId")
            }

            fn visit_str<E>(self, v: &str) -> Result<Self::Value, E>
            where
                E: serde::de::Error,
            {
                let parts: Vec<&str> = v.split('.').collect();

                match parts.as_slice() {
                    ["debug", "version"] => Ok(LineId::DebugVersion),
                    ["debug", "c_fps"] => Ok(LineId::DebugCfps),
                    ["debug", "c_mem"] => Ok(LineId::DebugCmem),
                    ["splash", "disclaimer"] => Ok(LineId::SplashDisclaimer),
                    ["menu", "last_savegame", "found"] => Ok(LineId::MenuLastsavegameFound),
                    ["menu", "last_savegame", "empty"] => Ok(LineId::MenuLastsavegameEmpty),
                    ["menu", "pressStart"] => Ok(LineId::MenuPressstart),
                    ["menu", "playGame"] => Ok(LineId::MenuPlaygame),
                    ["menu", "options"] => Ok(LineId::MenuOptions),
                    ["menu", "quit"] => Ok(LineId::MenuQuit),
                    ["menu", "continue"] => Ok(LineId::MenuContinue),
                    ["savegame", "title"] => Ok(LineId::SavegameTitle),
                    ["savegame", "new"] => Ok(LineId::SavegameNew),
                    ["savegame", "points"] => Ok(LineId::SavegamePoints),
                    ["savegame", "multiplier"] => Ok(LineId::SavegameMultiplier),
                    ["savegame", "multiplier", "count"] => Ok(LineId::SavegameMultiplierCount),
                    ["savegame", "purchased"] => Ok(LineId::SavegamePurchased),
                    ["savegame", "your_name"] => Ok(LineId::SavegameYourname),
                    ["options", "title"] => Ok(LineId::OptionsTitle),
                    ["options", "music"] => Ok(LineId::OptionsMusic),
                    ["options", "sound"] => Ok(LineId::OptionsSound),
                    ["options", "vsync"] => Ok(LineId::OptionsVsync),
                    ["options", "fullscreen"] => Ok(LineId::OptionsFullscreen),
                    ["options", "language"] => Ok(LineId::OptionsLanguage),
                    ["options", "debug"] => Ok(LineId::OptionsDebug),
                    ["options", "reset"] => Ok(LineId::OptionsReset),
                    ["options", "close"] => Ok(LineId::OptionsClose),
                    ["options", "general"] => Ok(LineId::OptionsGeneral),
                    ["options", "video"] => Ok(LineId::OptionsVideo),
                    ["options", "audio"] => Ok(LineId::OptionsAudio),
                    ["game", "petShop"] => Ok(LineId::GamePetshop),
                    ["game", "points"] => Ok(LineId::GamePoints),
                    ["game", "multiplier"] => Ok(LineId::GameMultiplier),
                    ["game", "newPoint"] => Ok(LineId::GameNewpoint),
                    ["dialogs", "not_enough_points"] => Ok(LineId::DialogsNotenoughpoints),
                    ["gifts", "empty"] => Ok(LineId::GiftsEmpty),
                    ["gifts", "multiplier"] => Ok(LineId::GiftsMultiplier),
                    ["gifts", "points"] => Ok(LineId::GiftsPoints),
                    ["gifts", "new_pet"] => Ok(LineId::GiftsNewpet),
                    ["game", "inventory", "title"] => Ok(LineId::GameInventoryTitle),
                    ["pet", "bror", "name"] => Ok(LineId::PetBrorName),
                    ["pet", "bror", "desc"] => Ok(LineId::PetBrorDesc),
                    ["pet", "sandwich", "name"] => Ok(LineId::PetSandwichName),
                    ["pet", "sandwich", "desc"] => Ok(LineId::PetSandwichDesc),
                    ["pet", "manlooshka", "name"] => Ok(LineId::PetManlooshkaName),
                    ["pet", "manlooshka", "desc"] => Ok(LineId::PetManlooshkaDesc),
                    ["pet", "thirsty", "name"] => Ok(LineId::PetThirstyName),
                    ["pet", "thirsty", "desc"] => Ok(LineId::PetThirstyDesc),
                    ["pet", "furios", "name"] => Ok(LineId::PetFuriosName),
                    ["pet", "furios", "desc"] => Ok(LineId::PetFuriosDesc),
                    ["pet", "tvcat", "name"] => Ok(LineId::PetTvcatName),
                    ["pet", "tvcat", "desc"] => Ok(LineId::PetTvcatDesc),
                    ["pet", "progcat", "name"] => Ok(LineId::PetProgcatName),
                    ["pet", "progcat", "desc"] => Ok(LineId::PetProgcatDesc),
                    ["pet", "screamcat", "name"] => Ok(LineId::PetScreamcatName),
                    ["pet", "screamcat", "desc"] => Ok(LineId::PetScreamcatDesc),
                    ["pet", "hellcat", "name"] => Ok(LineId::PetHellcatName),
                    ["pet", "hellcat", "desc"] => Ok(LineId::PetHellcatDesc),
                    ["pet", "lurker", "name"] => Ok(LineId::PetLurkerName),
                    ["pet", "lurker", "desc"] => Ok(LineId::PetLurkerDesc),
                    ["pet", "piano", "name"] => Ok(LineId::PetPianoName),
                    ["pet", "piano", "desc"] => Ok(LineId::PetPianoDesc),
                    ["pet", "bee", "name"] => Ok(LineId::PetBeeName),
                    ["pet", "bee", "desc"] => Ok(LineId::PetBeeDesc),
                    ["pet", "busy", "name"] => Ok(LineId::PetBusyName),
                    ["pet", "busy", "desc"] => Ok(LineId::PetBusyDesc),
                    ["pet", "aeae", "name"] => Ok(LineId::PetAeaeName),
                    ["pet", "aeae", "desc"] => Ok(LineId::PetAeaeDesc),
                    ["pet", "succat", "name"] => Ok(LineId::PetSuccatName),
                    ["pet", "succat", "desc"] => Ok(LineId::PetSuccatDesc),
                    _ => Err(Error::custom("unknown variant")),
                }
            }
        }

        deserializer.deserialize_str(CustomVisitor)
    }
}

#[derive(Deserialize, Resource, TypePath, TypeUuid, Clone, Asset)]
#[uuid = "413be529-bfeb-41b3-9db0-4b8b380a2c46"]
pub struct Localization(pub HashMap<LineId, String>);

impl Localization {
    pub fn get_literal_line(&self, line_id: LineId) -> Option<String> {
        if let Some(line) = self.0.get(&line_id) {
            return Some(line.into());
        }

        None
    }

    pub fn get_formatted_line(&self, line_id: LineId, parameters: Vec<String>) -> Option<String> {
        if let Some(line) = self.get_literal_line(line_id) {
            return Some(line.split("{}").enumerate().fold(
                String::new(),
                |mut acc: String, (i, part)| {
                    acc.push_str(part);

                    if i < parameters.len() {
                        acc.push_str(parameters.get(i).unwrap());
                    }

                    acc
                },
            ));
        }
        None
    }
}

pub fn init_localization(
    mut commands: Commands,
    settings: Res<Persistent<Settings>>,
    app_assets: Res<AppAssets>,
    string_assets: Res<Assets<Localization>>,
) {
    let locale = match settings.language.as_str() {
        "english" | _ => &app_assets.locale_english,
    };

    if let Some(asset) = string_assets.get(locale) {
        commands.insert_resource(asset.clone());
    }
}
