use std::str::FromStr;

use bevy::prelude::*;
use bevy_persistent::Persistent;
use serde::{Deserialize, Serialize};

use crate::{animation::Animation, assets::AppAssets};

use super::{item::Items, player::PlayerData, ui::UiInventory};

#[derive(Component)]
pub struct BuildingField(pub Building);

#[derive(Component, Deserialize, Serialize, Hash, Clone, PartialEq, Eq, PartialOrd, Ord)]
pub enum Building {
    Bedroom,
    Kitchen,
    Canyon,
    Sea,
    MissingNo,
}

#[derive(Component, Clone, PartialEq, Eq, PartialOrd, Ord)]
pub(super) struct BuildingComponent {
    pub index: isize,
}

#[derive(Resource)]
pub(super) struct BuildingResource {
    pub selected_index: isize,
}

#[derive(Clone)]
pub(super) enum BuildingCharacter {
    Static(Handle<Image>),
    Animated(Handle<TextureAtlas>, Animation),
}

impl Building {
    pub(super) fn get_image_handles(
        &self,
        assets: &Res<AppAssets>,
    ) -> (Handle<Image>, BuildingCharacter) {
        let timer = Timer::from_seconds(0.02, TimerMode::Repeating);

        match self {
            Self::Bedroom => (
                assets.building_bedroom_background.clone(),
                BuildingCharacter::Animated(
                    assets.cat_sleepy.clone(),
                    Animation {
                        timer,
                        frame_count: 75,
                        is_active: true,
                    },
                ),
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

pub(super) fn generate_buildings(mut commands: Commands, app_assets: Res<AppAssets>) {
    let mut pos = [0.0, -9.0, -4.0];

    for i in 0..3 {
        commands.spawn((
            SceneBundle {
                scene: app_assets.mdl_petbed.clone(),
                transform: Transform::from_xyz(pos[0], pos[1], pos[2]),
                ..default()
            },
            Name::new(format!("Building {}", i)),
            BuildingComponent { index: i },
        ));

        pos[0] += 4.0;
    }

    commands.insert_resource(BuildingResource { selected_index: 0 });
}

pub(super) fn update_building_position(
    resource: Res<BuildingResource>,
    mut query: Query<&mut Transform, With<BuildingComponent>>,
) {
    if resource.is_changed() {
        for (i, mut t) in query.iter_mut().enumerate() {
            let index = i as isize - resource.selected_index;
            let x = index as f32 * 4.0;

            t.translation.x = x;
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

        if let Some(count) = player_data.purchased_items.get(&bid) {
            // Clearing children
            if c.is_some() {
                let c = c.unwrap();

                if c.len() as i32 == *count {
                    continue;
                }

                commands.entity(be).clear_children();

                for child in c {
                    commands.entity(*child).despawn_recursive();
                }
            }

            // Identifying child image
            let handles = b.0.get_image_handles(&app_assets);
            let background = image_server.get(&handles.0).unwrap();

            // Generating positions
            let image_width = 32.0;
            let image_height = 32.0;

            let mut pos_x = -image_width;
            let mut pos_y = (t.translation.y + background.size().y as f32 * t.scale.y) / 2.0;

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

                let cid = match character {
                    BuildingCharacter::Animated(ta, a) => commands.spawn((
                        AtlasImageBundle {
                            texture_atlas_image: UiTextureAtlasImage {
                                index: 0,
                                ..default()
                            },
                            texture_atlas: ta,
                            style,
                            ..default()
                        },
                        a,
                    )),
                    BuildingCharacter::Static(i) => commands.spawn(ImageBundle {
                        image: UiImage::new(i),
                        style,
                        ..default()
                    }),
                }
                .id();

                commands.entity(be).add_child(cid);
            }
        }
    }
}
