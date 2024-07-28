use bevy::prelude::*;
use bevy_persistent::Persistent;
use bevy_sprite3d::{Sprite3d, Sprite3dParams};
use serde::Deserialize;

use crate::{
    animation::AnimationTimer, constants::PET_ENTITY_SPEED, persistent::Savegame, DataAssets,
    GUIAssets,
};

#[derive(Deserialize, Clone)]
pub struct Pet {
    pub id: String,
    pub price: f64,
    pub multiplier: f64,
    pub icon_data: PetIconData,
}

#[derive(Deserialize, Clone)]
pub struct PetIconData {
    pub columns: u32,
    pub rows: u32,
}

#[derive(Resource, Deserialize, TypePath, Clone, Asset)]
pub struct Pets(pub Vec<Pet>);

#[derive(Component)]
pub struct PetIdComponent(pub String);

#[derive(Component)]
pub struct PetEntityComponent(pub String);

pub fn pet_generation(
    mut commands: Commands,
    savegame: Res<Persistent<Savegame>>,
    query: Query<(Entity, &PetEntityComponent), With<PetEntityComponent>>,
    data_assets: Res<DataAssets>,
    pets_assets: Res<Assets<Pets>>,
    gui_assets: Res<GUIAssets>,
    mut sprite_params: Sprite3dParams,
) {
    let pets = pets_assets
        .get(data_assets.pets.id())
        .expect("Failed to get pets");

    let pets = &pets.0;

    for pet in pets {
        if let Some(amount) = savegame.pets.get(&pet.id) {
            let mut entities: Vec<(Entity, &PetEntityComponent)> =
                query.iter().filter(|x| x.1 .0.eq(&pet.id)).collect();
            let len = entities.len() as u32;
            let amount = *amount;

            if amount == len {
                continue;
            } else if amount > len {
                let mut icon: Option<Handle<Image>> = None;

                for icon_handle in &gui_assets.pet_icons {
                    if let Some(path) = icon_handle.path() {
                        if let Some(name) = path.path().file_name() {
                            let n = format!("{}.png", pet.id);
                            if name.to_str().unwrap().eq(&n) {
                                icon = Some(icon_handle.clone());
                                break;
                            }
                        }
                    }
                }

                if icon.is_none() {
                    icon = Some(gui_assets.pets.clone());
                }

                for _ in 0..amount - len {
                    commands.spawn((
                        Sprite3d {
                            image: icon.clone().unwrap(),
                            pixels_per_metre: 184.0,
                            alpha_mode: AlphaMode::Opaque,
                            transform: Transform::from_xyz(-10.0, 0.4, -10.0),
                            ..default()
                        }
                        .bundle_with_atlas(
                            &mut sprite_params,
                            TextureAtlas::from(gui_assets.pet_icon_layout.clone()),
                        ),
                        AnimationTimer(Timer::from_seconds(0.1, TimerMode::Repeating)),
                        PetEntityComponent(pet.id.clone()),
                    ));
                }
            } else {
                for _ in 0..len - amount {
                    let (e, _) = entities[0];

                    commands.entity(e).despawn();
                    entities.remove(0);
                }
            }
        }
    }
}

pub fn pet_revolution(time: Res<Time>, mut query: Query<&mut Transform, With<PetEntityComponent>>) {
    for (i, mut t) in query.iter_mut().enumerate() {
        let angle = PET_ENTITY_SPEED * time.delta_seconds();
        let (x, z) = (t.translation.x, t.translation.z);

        t.translation.x = x * angle.cos() - z * angle.sin();
        t.translation.z = x * angle.sin() + z * angle.cos();
    }
}
