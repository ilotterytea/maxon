use std::str::FromStr;

use bevy::prelude::*;
use bevy_persistent::Persistent;

use crate::{animation::Animation, assets::AppAssets};

use super::{item::Items, player::PlayerData, ui::UiInventory};

#[derive(Component)]
pub struct BuildingField(pub Building);

#[derive(Component, Clone, PartialEq, Eq, PartialOrd, Ord)]
pub enum Building {
    Bedroom,
    Kitchen,
    Canyon,
    Sea,
    MissingNo,
}

#[derive(Clone)]
pub enum BuildingCharacter {
    Static(Handle<Image>),
    Animated(Handle<TextureAtlas>, Animation),
}

impl Building {
    pub fn get_image_handles(&self, assets: &Res<AppAssets>) -> (Handle<Image>, BuildingCharacter) {
        match self {
            Self::Bedroom => (
                assets.building_bedroom_background.clone(),
                BuildingCharacter::Static(assets.icon.clone()),
            ),
            _ => (
                assets.icon.clone(),
                BuildingCharacter::Static(assets.icon.clone()),
            ),
        }
    }
}

impl FromStr for Building {
    type Err = Self;
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "Building.Bedroom" => Ok(Self::Bedroom),
            "Building.Kitchen" => Ok(Self::Kitchen),
            "Building.Canyon" => Ok(Self::Canyon),
            "Building.Sea" => Ok(Self::Sea),
            _ => Err(Self::MissingNo),
        }
    }
}

impl ToString for Building {
    fn to_string(&self) -> String {
        match self {
            Self::Bedroom => "Building.Bedroom".to_string(),
            Self::Kitchen => "Building.Kitchen".to_string(),
            Self::Canyon => "Building.Canyon".to_string(),
            Self::Sea => "Building.Sea".to_string(),
            Self::MissingNo => "Building.MissingNo".to_string(),
        }
    }
}

pub fn update_existing_buildings(
    mut commands: Commands,
    app_assets: Res<AppAssets>,
    player_data: Res<Persistent<PlayerData>>,
    image_server: Res<Assets<Image>>,
    sheet_server: Res<Assets<TextureAtlas>>,
    building_query: Query<
        (Entity, &BuildingField, Option<&Children>, &Transform),
        With<BuildingField>,
    >,
) {
    for (be, b, c, t) in building_query.iter() {
        let bid = b.0.to_string();
        // Clearing children
        commands.entity(be).clear_children();

        if c.is_some() {
            let c = c.unwrap();

            for child in c {
                commands.entity(*child).despawn_recursive();
            }
        }

        if let Some(count) = player_data.purchased_items.get(&bid) {
            // Identifying child image
            let handles = b.0.get_image_handles(&app_assets);
            let background = image_server.get(&handles.0).unwrap();
            //let character = match handles.1 { BuildingCharacter::Static(h) => image_server.get(&h).unwrap(), BuildingCharacter::Animated(t, a) => };

            // Generating positions
            let image_width = 32.0;
            let image_height = 32.0;

            let mut pos_x = -image_width;
            let mut pos_y = (t.translation.y + background.size().y * t.scale.y) / 2.0;

            // Generating children
            for i in 0..*count {
                let character = handles.1.clone();
                pos_x += image_width + 5.0;
                let y = image_height / 2.0;

                pos_y += if i % 2 == 0 { -y } else { y };

                let style = Style {
                    position_type: PositionType::Absolute,
                    left: Val::Px(pos_x),
                    bottom: Val::Px(pos_y),
                    width: Val::Px(image_width),
                    height: Val::Px(image_height),
                    ..default()
                };

                let transform = Transform::from_xyz(pos_x, pos_y, 0.0);

                let cid = match character {
                    BuildingCharacter::Animated(ta, a) => commands.spawn((
                        SpriteSheetBundle {
                            sprite: TextureAtlasSprite {
                                index: 0,
                                ..default()
                            },
                            texture_atlas: ta,
                            transform,
                            ..default()
                        },
                        a,
                    )),
                    BuildingCharacter::Static(i) => commands.spawn(ImageBundle {
                        image: UiImage::new(i),
                        style,
                        transform,
                        ..default()
                    }),
                }
                .id();

                commands.entity(be).add_child(cid);
            }
        }
    }
}
