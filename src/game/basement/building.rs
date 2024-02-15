use std::{str::FromStr, time::Duration};

use bevy::prelude::*;
use bevy_easings::{Ease, EaseMethod, EasingType};
use bevy_mod_billboard::BillboardTextBundle;
use bevy_persistent::Persistent;
use bevy_sprite3d::{AtlasSprite3d, Sprite3d, Sprite3dParams};
use bevy_turborand::prelude::*;
use serde::{Deserialize, Serialize};

use crate::{
    animation::Animation,
    assets::AppAssets,
    game::PlayerData,
    localization::{LineId, Localization},
    style::get_building_header_text_style,
};

use super::ui::BuildingMovementButton;

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

    pub(super) fn get_scene_handle(&self, assets: &Res<AppAssets>) -> Handle<Scene> {
        match self {
            _ => assets.mdl_petbed.clone(),
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

#[derive(Resource)]
pub struct Buildings(pub Vec<BuildingData>);

pub struct BuildingData {
    pub building: Building,
    pub price: f32,
    pub multiplier: f32,
}

pub(super) fn init_buildings(mut commands: Commands) {
    let buildings = vec![
        BuildingData {
            building: Building::Bedroom,
            price: 15.0,
            multiplier: 0.1,
        },
        BuildingData {
            building: Building::Kitchen,
            price: 100.0,
            multiplier: 1.0,
        },
        BuildingData {
            building: Building::Canyon,
            price: 1100.0,
            multiplier: 8.0,
        },
        BuildingData {
            building: Building::Sea,
            price: 12000.0,
            multiplier: 48.0,
        },
    ];

    commands.insert_resource(Buildings(buildings));
}

pub(super) fn generate_buildings(
    mut commands: Commands,
    app_assets: Res<AppAssets>,
    locale: Res<Localization>,
    buildings: Res<Buildings>,
    savegame: Res<Persistent<PlayerData>>,
) {
    let mut pos = [0.0, -9.0, -4.0];

    for (i, data) in buildings.0.iter().enumerate() {
        let scene = if savegame
            .buildings
            .iter()
            .any(|(k, v)| k.eq(&data.building) && v.ne(&0))
        {
            data.building.get_scene_handle(&app_assets)
        } else {
            app_assets.mdl_building_unknown.clone()
        };

        commands
            .spawn((
                SceneBundle {
                    scene,
                    transform: Transform::from_xyz(pos[0], pos[1], pos[2])
                        .with_scale(Vec3::splat(0.5)),
                    ..default()
                },
                Name::new(format!("Building {}", i)),
                BuildingComponent { index: i as isize },
                data.building.clone(),
            ))
            .with_children(|parent| {
                parent.spawn(BillboardTextBundle {
                    text: Text::from_section(
                        {
                            let name = format!("{}.name", data.building.to_string().to_lowercase());

                            if let Some(line_id) = LineId::from_str(name.as_str()) {
                                if let Some(line) = locale.get_literal_line(line_id) {
                                    line
                                } else {
                                    name
                                }
                            } else {
                                name
                            }
                        },
                        get_building_header_text_style(app_assets.font_text.clone()),
                    ),
                    transform: Transform::from_xyz(0.0, 2.0, 0.0).with_scale(Vec3::splat(0.013)),
                    ..default()
                });
            });

        pos[0] += 4.0;
    }

    commands.insert_resource(BuildingResource { selected_index: 0 });
}

pub(super) fn update_building_position(
    mut commands: Commands,
    resource: Res<BuildingResource>,
    mut query: Query<(&mut Transform, Entity), With<BuildingComponent>>,
) {
    if resource.is_changed() {
        for (i, (t, e)) in query.iter_mut().enumerate() {
            let index = i as isize - resource.selected_index;
            let x = index as f32 * 4.0;

            let mut t2 = *t;

            t2.scale = Vec3::splat(if resource.selected_index == i as isize {
                1.0
            } else {
                0.5
            });

            t2.translation.x = x;

            let component = t.ease_to(
                t2,
                EaseMethod::Linear,
                EasingType::Once {
                    duration: Duration::from_millis(250),
                },
            );

            commands.entity(e).insert(component);
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
    keyboard_input: Res<Input<KeyCode>>,
) {
    let mut index_delta = 0;

    for (i, b) in button_query.iter() {
        if *i == Interaction::Pressed {
            match *b {
                BuildingMovementButton::Left => index_delta = -1,
                BuildingMovementButton::Right => index_delta = 1,
            }
        }
    }

    if keyboard_input.just_pressed(KeyCode::A) || keyboard_input.just_pressed(KeyCode::Left) {
        index_delta = -1;
    }

    if keyboard_input.just_pressed(KeyCode::D) || keyboard_input.just_pressed(KeyCode::Right) {
        index_delta = 1;
    }

    let building_len = building_query.iter().len() as isize;

    if (0..building_len).contains(&(building_resource.selected_index + index_delta)) {
        building_resource.selected_index += index_delta;
    }
}

#[derive(Component)]
pub struct BuildingUnit;

pub(super) fn update_building_units(
    mut commands: Commands,
    app_assets: Res<AppAssets>,
    building_query: Query<(Entity, &Building, &Children), With<Building>>,
    building_unit_query: Query<Entity, With<BuildingUnit>>,
    savegame: Res<Persistent<PlayerData>>,
    mut sprite_params: Sprite3dParams,
    mut rng: ResMut<GlobalRng>,
) {
    for (e, b, c) in building_query.iter() {
        if let Some(amount) = savegame.buildings.get(b) {
            let children = c
                .iter()
                .filter(|x| building_unit_query.iter().any(|y| (*x).eq(&y)))
                .collect::<Vec<&Entity>>();

            let difference = amount - children.len();

            if difference == 0 {
                continue;
            }

            let image_handles = b.get_image_handles(&app_assets);
            let character_image_handle = image_handles.1;

            for _ in 0..difference {
                let pos_range = -8..8;
                let scale_range = 2..5;

                let scale = rng.usize(scale_range) as f32 / 10.0;

                let transform = Transform::from_xyz(
                    rng.isize(pos_range.clone()) as f32 / 10.0,
                    0.25,
                    rng.isize(pos_range) as f32 / 10.0,
                )
                .with_scale(Vec3::new(scale, scale, scale));

                let id = match character_image_handle {
                    BuildingCharacter::Static(ref v) => commands.spawn(
                        Sprite3d {
                            image: v.clone(),
                            transform,
                            ..default()
                        }
                        .bundle(&mut sprite_params),
                    ),

                    BuildingCharacter::Animated(ref v, ref a) => commands.spawn((
                        AtlasSprite3d {
                            atlas: v.clone(),
                            index: 0,
                            transform,
                            ..default()
                        }
                        .bundle(&mut sprite_params),
                        a.clone(),
                    )),
                }
                .insert(BuildingUnit)
                .id();

                commands.entity(e).add_child(id);
            }
        }
    }
}
