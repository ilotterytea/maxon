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
pub(super) struct UnitRoot;

#[derive(Component)]
pub(super) struct UnitParent;

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
        .spawn((SpatialBundle::default(), Name::new("Buildings"), UnitRoot))
        .id();

    for b in buildings {
        if let Some(amount) = savegame.buildings.get(&b.building) {
            if *amount == 0 {
                continue;
            }

            let e = commands
                .spawn((
                    SpatialBundle::default(),
                    Name::new(b.building.to_string()),
                    UnitParent,
                    b.building.clone(),
                ))
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

pub fn update_unit_amount(
    mut commands: Commands,
    savegame: Res<Persistent<PlayerData>>,
    mut rng: ResMut<GlobalRng>,
    mut sprite_params: Sprite3dParams,
    app_assets: Res<AppAssets>,
    camera_query: Query<&Transform, With<Camera>>,
    root_query: Query<Entity, (With<UnitRoot>, Without<UnitParent>, Without<Unit>)>,
    parent_query: Query<
        (&Building, &Children, Entity),
        (With<Building>, With<UnitParent>, Without<Unit>),
    >,
) {
    let camera_transform = camera_query.single();
    let root = root_query.single();

    for (building, amount) in savegame.buildings.iter() {
        let (building, b_children, b_entity) = match parent_query.iter().find(|x| x.0.eq(building))
        {
            Some(v) => (v.0, Some(v.1), v.2),
            None => {
                let e = commands
                    .spawn((
                        SpatialBundle::default(),
                        Name::new(building.to_string()),
                        UnitParent,
                        building.clone(),
                    ))
                    .id();

                commands.entity(root).add_child(e);

                (building, None, e)
            }
        };

        let children_amount = match b_children {
            Some(v) => v.len(),
            None => 0,
        };

        let difference: isize = *amount as isize - children_amount as isize;

        if difference == 0 {
            continue;
        }

        if difference.is_positive() {
            for _ in 0..difference {
                let id = generate_unit(
                    &mut commands,
                    building.clone(),
                    &mut rng,
                    camera_transform,
                    &app_assets,
                    &mut sprite_params,
                );

                commands.entity(b_entity).add_child(id);
            }
        }

        if let Some(b_children) = b_children {
            if difference.is_negative() {
                for _ in difference..0 {
                    let u_entity = b_children[rng.usize(0..b_children.len())];

                    commands.entity(b_entity).remove_children(&[u_entity]);
                    commands.entity(u_entity).despawn_recursive();
                }
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
