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

    BuildingBedroomName,
    BuildingBedroomDesc,

    BuildingKitchenName,
    BuildingKitchenDesc,

    BuildingCanyonName,
    BuildingCanyonDesc,

    BuildingSeaName,
    BuildingSeaDesc,

    BuildingBathroomName,
    BuildingBathroomDesc,

    BuildingLivingroomName,
    BuildingLivingroomDesc,

    BuildingServerroomName,
    BuildingServerroomDesc,

    BuildingAsylumName,
    BuildingAsylumDesc,

    BuildingHellName,
    BuildingHellDesc,

    BuildingAbyssName,
    BuildingAbyssDesc,

    BuildingBallroomName,
    BuildingBallroomDesc,

    BuildingMeadowName,
    BuildingMeadowDesc,

    BuildingOfficeName,
    BuildingOfficeDesc,

    BuildingAeaeName,
    BuildingAeaeDesc,

    BuildingKindergardenName,
    BuildingKindergardenDesc,
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
            ["building", "bedroom", "name"] => Some(LineId::BuildingBedroomName),
            ["building", "bedroom", "desc"] => Some(LineId::BuildingBedroomDesc),
            ["building", "kitchen", "name"] => Some(LineId::BuildingKitchenName),
            ["building", "kitchen", "desc"] => Some(LineId::BuildingKitchenDesc),
            ["building", "canyon", "name"] => Some(LineId::BuildingCanyonName),
            ["building", "canyon", "desc"] => Some(LineId::BuildingCanyonDesc),
            ["building", "sea", "name"] => Some(LineId::BuildingSeaName),
            ["building", "sea", "desc"] => Some(LineId::BuildingSeaDesc),
            ["building", "bathroom", "name"] => Some(LineId::BuildingBathroomName),
            ["building", "bathroom", "desc"] => Some(LineId::BuildingBathroomDesc),
            ["building", "living_room", "name"] => Some(LineId::BuildingLivingroomName),
            ["building", "living_room", "desc"] => Some(LineId::BuildingLivingroomDesc),
            ["building", "server_room", "name"] => Some(LineId::BuildingServerroomName),
            ["building", "server_room", "desc"] => Some(LineId::BuildingServerroomDesc),
            ["building", "asylum", "name"] => Some(LineId::BuildingAsylumName),
            ["building", "asylum", "desc"] => Some(LineId::BuildingAsylumDesc),
            ["building", "hell", "name"] => Some(LineId::BuildingHellName),
            ["building", "hell", "desc"] => Some(LineId::BuildingHellDesc),
            ["building", "abyss", "name"] => Some(LineId::BuildingAbyssName),
            ["building", "abyss", "desc"] => Some(LineId::BuildingAbyssDesc),
            ["building", "ballroom", "name"] => Some(LineId::BuildingBallroomName),
            ["building", "ballroom", "desc"] => Some(LineId::BuildingBallroomDesc),
            ["building", "meadow", "name"] => Some(LineId::BuildingMeadowName),
            ["building", "meadow", "desc"] => Some(LineId::BuildingMeadowDesc),
            ["building", "office", "name"] => Some(LineId::BuildingOfficeName),
            ["building", "office", "desc"] => Some(LineId::BuildingOfficeDesc),
            ["building", "aeae", "name"] => Some(LineId::BuildingAeaeName),
            ["building", "aeae", "desc"] => Some(LineId::BuildingAeaeDesc),
            ["building", "kindergarden", "name"] => Some(LineId::BuildingKindergardenName),
            ["building", "kindergarden", "desc"] => Some(LineId::BuildingKindergardenDesc),
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
                    ["building", "bedroom", "name"] => Ok(LineId::BuildingBedroomName),
                    ["building", "bedroom", "desc"] => Ok(LineId::BuildingBedroomDesc),
                    ["building", "kitchen", "name"] => Ok(LineId::BuildingKitchenName),
                    ["building", "kitchen", "desc"] => Ok(LineId::BuildingKitchenDesc),
                    ["building", "canyon", "name"] => Ok(LineId::BuildingCanyonName),
                    ["building", "canyon", "desc"] => Ok(LineId::BuildingCanyonDesc),
                    ["building", "sea", "name"] => Ok(LineId::BuildingSeaName),
                    ["building", "sea", "desc"] => Ok(LineId::BuildingSeaDesc),
                    ["building", "bathroom", "name"] => Ok(LineId::BuildingBathroomName),
                    ["building", "bathroom", "desc"] => Ok(LineId::BuildingBathroomDesc),
                    ["building", "living_room", "name"] => Ok(LineId::BuildingLivingroomName),
                    ["building", "living_room", "desc"] => Ok(LineId::BuildingLivingroomDesc),
                    ["building", "server_room", "name"] => Ok(LineId::BuildingServerroomName),
                    ["building", "server_room", "desc"] => Ok(LineId::BuildingServerroomDesc),
                    ["building", "asylum", "name"] => Ok(LineId::BuildingAsylumName),
                    ["building", "asylum", "desc"] => Ok(LineId::BuildingAsylumDesc),
                    ["building", "hell", "name"] => Ok(LineId::BuildingHellName),
                    ["building", "hell", "desc"] => Ok(LineId::BuildingHellDesc),
                    ["building", "abyss", "name"] => Ok(LineId::BuildingAbyssName),
                    ["building", "abyss", "desc"] => Ok(LineId::BuildingAbyssDesc),
                    ["building", "ballroom", "name"] => Ok(LineId::BuildingBallroomName),
                    ["building", "ballroom", "desc"] => Ok(LineId::BuildingBallroomDesc),
                    ["building", "meadow", "name"] => Ok(LineId::BuildingMeadowName),
                    ["building", "meadow", "desc"] => Ok(LineId::BuildingMeadowDesc),
                    ["building", "office", "name"] => Ok(LineId::BuildingOfficeName),
                    ["building", "office", "desc"] => Ok(LineId::BuildingOfficeDesc),
                    ["building", "aeae", "name"] => Ok(LineId::BuildingAeaeName),
                    ["building", "aeae", "desc"] => Ok(LineId::BuildingAeaeDesc),
                    ["building", "kindergarden", "name"] => Ok(LineId::BuildingKindergardenName),
                    ["building", "kindergarden", "desc"] => Ok(LineId::BuildingKindergardenDesc),
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
