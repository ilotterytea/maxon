use bevy::{
    prelude::*,
    reflect::{TypePath, TypeUuid},
    utils::HashMap,
};
use serde::Deserialize;

#[derive(Deserialize, PartialEq, Eq, Hash)]
pub enum LineId {
    ItemBror,
}

#[derive(Deserialize, Resource, TypePath, TypeUuid)]
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
