use std::str::FromStr;

use bevy::prelude::*;
use bevy_persistent::Persistent;
use serde::{Deserialize, Serialize};

use crate::{animation::Animation, assets::AppAssets};

use super::{player::PlayerData, ui::BuildingMovementButton};

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

pub(super) fn update_selected_building_index(
    mut building_resource: ResMut<BuildingResource>,
    button_query: Query<
        (&Interaction, &BuildingMovementButton),
        (With<BuildingMovementButton>, Changed<Interaction>),
    >,
    building_query: Query<Entity, With<Building>>,
) {
    let mut index_delta = 0;

    for (i, b) in button_query.iter() {
        if b.eq(&BuildingMovementButton::Left) {
            match *i {
                Interaction::Pressed => {
                    index_delta -= 1;
                }
                _ => {}
            }
        } else if b.eq(&BuildingMovementButton::Right) {
            match *i {
                Interaction::Pressed => {
                    index_delta += 1;
                }
                _ => {}
            }
        }
    }

    let building_len = building_query.iter().len() as isize;

    if (0..building_len).contains(&(building_resource.selected_index + index_delta)) {
        building_resource.selected_index += index_delta;
    }
}
