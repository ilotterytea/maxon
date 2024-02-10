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
    CategoryShopHeader,
    CategoryInventoryHeader,
    ItemBror,
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
