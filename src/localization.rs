use std::collections::HashMap;

use bevy::prelude::*;
use bevy_persistent::Persistent;
use serde::{de::Visitor, Deserialize};

use crate::{persistent::Settings, DataAssets};

#[derive(Clone, PartialEq, Eq, Hash)]
pub enum LineId {
    SplashDisclaimer,

    MenuPressStart,
    MenuNewGame,
    MenuContinue,
    MenuReset,

    StoreTitle,
    StoreMultiplier1x,
    StoreMultiplier10x,
    StoreModeBuy,
    StoreModeSell,

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
    fn as_slice() -> &'static [&'static str] {
        &[
            "splash.disclaimer",
            "menu.press_start",
            "menu.new_game",
            "menu.continue",
            "menu.reset",
            "store.title",
            "store.multiplier.1x",
            "store.multiplier.10x",
            "store.mode.buy",
            "store.mode.sell",
            "pet.bror.name",
            "pet.bror.desc",
            "pet.sandwich.name",
            "pet.sandwich.desc",
            "pet.manlooshka.name",
            "pet.manlooshka.desc",
            "pet.thirsty.name",
            "pet.thirsty.desc",
            "pet.furios.name",
            "pet.furios.desc",
            "pet.tvcat.name",
            "pet.tvcat.desc",
            "pet.progcat.name",
            "pet.progcat.desc",
            "pet.screamcat.name",
            "pet.screamcat.desc",
            "pet.hellcat.name",
            "pet.hellcat.desc",
            "pet.lurker.name",
            "pet.lurker.desc",
            "pet.piano.name",
            "pet.piano.desc",
            "pet.bee.name",
            "pet.bee.desc",
            "pet.busy.name",
            "pet.busy.desc",
            "pet.aeae.name",
            "pet.aeae.desc",
            "pet.succat.name",
            "pet.succat.desc",
        ]
    }
}

impl<'de> Deserialize<'de> for LineId {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        deserializer.deserialize_str(LineIdVisitor)
    }
}

struct LineIdVisitor;

impl<'de> Visitor<'de> for LineIdVisitor {
    type Value = LineId;

    fn expecting(&self, formatter: &mut std::fmt::Formatter) -> std::fmt::Result {
        formatter.write_str("a string representing an enum variant")
    }

    fn visit_str<E>(self, v: &str) -> Result<Self::Value, E>
    where
        E: serde::de::Error,
    {
        match v {
            "splash.disclaimer" => Ok(LineId::SplashDisclaimer),
            "menu.press_start" => Ok(LineId::MenuPressStart),
            "menu.new_game" => Ok(LineId::MenuNewGame),
            "menu.continue" => Ok(LineId::MenuContinue),
            "menu.reset" => Ok(LineId::MenuReset),
            "store.title" => Ok(LineId::StoreTitle),
            "store.multiplier.1x" => Ok(LineId::StoreMultiplier1x),
            "store.multiplier.10x" => Ok(LineId::StoreMultiplier10x),
            "store.mode.buy" => Ok(LineId::StoreModeBuy),
            "store.mode.sell" => Ok(LineId::StoreModeSell),
            "pet.bror.name" => Ok(LineId::PetBrorName),
            "pet.bror.desc" => Ok(LineId::PetBrorDesc),
            "pet.sandwich.name" => Ok(LineId::PetSandwichName),
            "pet.sandwich.desc" => Ok(LineId::PetSandwichDesc),
            "pet.manlooshka.name" => Ok(LineId::PetManlooshkaName),
            "pet.manlooshka.desc" => Ok(LineId::PetManlooshkaDesc),
            "pet.thirsty.name" => Ok(LineId::PetThirstyName),
            "pet.thirsty.desc" => Ok(LineId::PetThirstyDesc),
            "pet.furios.name" => Ok(LineId::PetFuriosName),
            "pet.furios.desc" => Ok(LineId::PetFuriosDesc),
            "pet.tvcat.name" => Ok(LineId::PetTvcatName),
            "pet.tvcat.desc" => Ok(LineId::PetTvcatDesc),
            "pet.progcat.name" => Ok(LineId::PetProgcatName),
            "pet.progcat.desc" => Ok(LineId::PetProgcatDesc),
            "pet.screamcat.name" => Ok(LineId::PetScreamcatName),
            "pet.screamcat.desc" => Ok(LineId::PetScreamcatDesc),
            "pet.hellcat.name" => Ok(LineId::PetHellcatName),
            "pet.hellcat.desc" => Ok(LineId::PetHellcatDesc),
            "pet.lurker.name" => Ok(LineId::PetLurkerName),
            "pet.lurker.desc" => Ok(LineId::PetLurkerDesc),
            "pet.piano.name" => Ok(LineId::PetPianoName),
            "pet.piano.desc" => Ok(LineId::PetPianoDesc),
            "pet.bee.name" => Ok(LineId::PetBeeName),
            "pet.bee.desc" => Ok(LineId::PetBeeDesc),
            "pet.busy.name" => Ok(LineId::PetBusyName),
            "pet.busy.desc" => Ok(LineId::PetBusyDesc),
            "pet.aeae.name" => Ok(LineId::PetAeaeName),
            "pet.aeae.desc" => Ok(LineId::PetAeaeDesc),
            "pet.succat.name" => Ok(LineId::PetSuccatName),
            "pet.succat.desc" => Ok(LineId::PetSuccatDesc),
            _ => Err(E::unknown_variant(v, LineId::as_slice())),
        }
    }
}

#[derive(Resource, Deserialize, TypePath, Clone, Asset)]
pub struct Localization(pub HashMap<LineId, String>);

pub fn setup_localization(
    mut commands: Commands,
    savegame: Res<Persistent<Settings>>,
    data_assets: Res<DataAssets>,
    localization_assets: Res<Assets<Localization>>,
) {
    let localizations = &data_assets.localizations;

    let mut locale: Option<Localization> = None;
    let mut default_locale: Option<Localization> = None;

    for localization in localizations {
        if let (Some(path), Some(locale_2)) = (
            localization.path(),
            localization_assets.get(localization.id()),
        ) {
            if let Some(name) = path.path().file_name() {
                let name = name.to_str().unwrap().strip_suffix(".locale.json").unwrap();
                if name.eq("en_us") {
                    default_locale = Some(locale_2.clone());
                }
                if name.eq(savegame.language.as_str()) {
                    locale = Some(locale_2.clone());
                    break;
                }
            }
        }
    }

    if locale.is_none() {
        locale = Some(default_locale.unwrap());
    }

    commands.insert_resource(locale.unwrap());
}
