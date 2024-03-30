use std::ops::Range;

use bevy::prelude::*;
use bevy_persistent::Persistent;
use bevy_sprite3d::{AtlasSprite3d, Sprite3d, Sprite3dParams};
use bevy_turborand::{DelegatedRng, GlobalRng};

use crate::{
    assets::AppAssets,
    game::{
        basement::building::{Building, BuildingCharacter, Buildings},
        PlayerData,
    },
};

const UNIT_AREA_SPAWN: (Range<isize>, Range<isize>) = (-80..0, -60..0);

#[derive(Component)]
pub struct Unit;

pub fn spawn_units(
    mut commands: Commands,
    buildings: Res<Buildings>,
    app_assets: Res<AppAssets>,
    savegame: Res<Persistent<PlayerData>>,
    mut sprite_params: Sprite3dParams,
    mut rng: ResMut<GlobalRng>,
    camera_query: Query<&Transform, With<Camera>>,
) {
    let camera_transform = camera_query.single();

    let buildings = buildings.0.iter();

    let parent = commands
        .spawn((SpatialBundle::default(), Name::new("Buildings")))
        .id();

    for b in buildings {
        if let Some(amount) = savegame.buildings.get(&b.building) {
            if *amount == 0 {
                continue;
            }

            let (_, character) = b.building.get_image_handles(&app_assets);

            let e = commands
                .spawn((SpatialBundle::default(), Name::new(b.building.to_string())))
                .id();

            commands.entity(parent).add_child(e);

            for _ in 0..*amount {
                let id = generate_unit(
                    &mut commands,
                    b.building.clone(),
                    &mut rng,
                    camera_transform,
                    &app_assets,
                    &mut sprite_params,
                );

                commands.entity(e).add_child(id);
            }
        }
    }
}

fn generate_unit(
    commands: &mut Commands,
    building: Building,
    rng: &mut ResMut<GlobalRng>,
    camera_transform: &Transform,
    app_assets: &Res<AppAssets>,
    mut sprite_params: &mut Sprite3dParams,
) -> Entity {
    let (_, character) = building.get_image_handles(app_assets);

    let x = rng.isize(UNIT_AREA_SPAWN.0) as f32 / 10.0;
    let z = rng.isize(UNIT_AREA_SPAWN.1) as f32 / 10.0;

    let transform =
        Transform::from_xyz(x, 0.8, z).looking_at(camera_transform.translation, Vec3::Y);

    let unit = Unit;

    let name = Name::new(building.to_string());

    match character {
        BuildingCharacter::Static(ref v) => commands.spawn((
            Sprite3d {
                image: v.clone(),
                transform,
                ..default()
            }
            .bundle(&mut sprite_params),
            unit,
            name,
        )),

        BuildingCharacter::Animated(ref v, ref a) => commands.spawn((
            AtlasSprite3d {
                atlas: v.clone(),
                index: 0,
                transform,
                ..default()
            }
            .bundle(&mut sprite_params),
            a.clone(),
            unit,
            name,
        )),
    }
    .id()
}
