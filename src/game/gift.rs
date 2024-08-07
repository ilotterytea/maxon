use std::f32::consts::PI;

use bevy::prelude::*;
use bevy_mod_billboard::{
    BillboardMeshHandle, BillboardTextBundle, BillboardTextureBundle, BillboardTextureHandle,
};
use bevy_mod_picking::prelude::*;
use bevy_persistent::Persistent;
use bevy_sprite3d::{Sprite3d, Sprite3dParams};
use rand::Rng;

use crate::{
    persistent::Savegame, style::get_text_style_default, FontAssets, GUIAssets, ModelAssets,
    SFXAssets, SpriteAssets,
};

use super::{
    components::GameObjectComponent, shop::systems::PurchaseEvent,
    systems::ImNotLookingAtCameraComponent,
};

#[derive(Component)]
pub struct GiftboxNotificationText;

#[derive(Component)]
pub struct GiftboxNotificationIcon;

#[derive(Component)]
pub struct GiftboxOpenSound;

#[derive(Component)]
pub struct GiftboxRays;

#[derive(Component)]
pub struct GiftboxRaysLight;

#[derive(Component)]
pub struct GiftboxComponent(pub GiftboxStatus);

#[derive(PartialEq, Eq)]
pub enum GiftboxStatus {
    Locked,
    Opened,
}

#[derive(Resource)]
pub struct GiftboxTimer(pub Timer);

pub fn setup_gift_box(mut commands: Commands, model_assets: Res<ModelAssets>) {
    commands.spawn((
        SceneBundle {
            scene: model_assets.chest_prop.clone(),
            transform: Transform::from_translation(Vec3::new(6.8, 0.0, 1.0))
                .with_scale(Vec3::new(2.0, 2.0, 2.0))
                .with_rotation(Quat::from_rotation_y(180.0 * PI / 180.0)),
            ..default()
        },
        Name::new("Gift box"),
        On::<Pointer<Click>>::run(click_on_gift_box),
        GiftboxComponent(GiftboxStatus::Locked),
        GameObjectComponent,
    ));

    commands.insert_resource(GiftboxTimer(Timer::from_seconds(
        5.0 * 60.0,
        TimerMode::Repeating,
    )));
}

pub fn update_gift_box(
    mut commands: Commands,
    mut gift_box_query: Query<(Entity, &Transform, &mut GiftboxComponent), With<GiftboxComponent>>,
    time: Res<Time>,
    mut gift_box_timer: ResMut<GiftboxTimer>,
    model_assets: Res<ModelAssets>,
    sprite_assets: Res<SpriteAssets>,
    sfx_assets: Res<SFXAssets>,
    mut sprite_3d_params: Sprite3dParams,
) {
    gift_box_timer.0.tick(time.delta());

    if !gift_box_timer.0.just_finished() {
        return;
    }

    for (e, transform, mut comp) in gift_box_query.iter_mut() {
        if comp.0 == GiftboxStatus::Opened {
            continue;
        }

        commands.spawn((
            AudioBundle {
                source: sfx_assets.chest_opened.clone(),
                settings: PlaybackSettings {
                    mode: bevy::audio::PlaybackMode::Loop,
                    ..default()
                },
            },
            GiftboxOpenSound,
            GameObjectComponent,
        ));

        commands.entity(e).remove::<SceneBundle>();
        commands.entity(e).insert(SceneBundle {
            scene: model_assets.chest_opened_prop.clone(),
            transform: transform.clone(),
            ..default()
        });

        comp.0 = GiftboxStatus::Opened;

        commands.spawn((
            Sprite3d {
                image: sprite_assets.rays.clone(),
                pixels_per_metre: 48.0,
                alpha_mode: AlphaMode::Blend,
                transform: Transform::from_xyz(6.5, 0.8, 1.0)
                    .with_rotation(Quat::from_rotation_y(90.0 * PI / 180.0))
                    .with_scale(Vec3::splat(0.3)),
                ..default()
            }
            .bundle(&mut sprite_3d_params),
            Name::new("Gift box rays"),
            GiftboxRays,
            ImNotLookingAtCameraComponent,
            GameObjectComponent,
        ));

        commands.spawn((
            PointLightBundle {
                point_light: PointLight {
                    color: bevy::color::palettes::css::ORANGE.into(),
                    intensity: 200000.0,
                    radius: 200.0,
                    range: 100.0,
                    shadows_enabled: false,
                    ..default()
                },
                transform: Transform::from_xyz(5.6, 2.0, 0.8),
                ..default()
            },
            Name::new("Gift box rays point light"),
            GiftboxRaysLight,
            GameObjectComponent,
        ));
    }
}

pub fn click_on_gift_box(
    mut commands: Commands,
    mut savegame: ResMut<Persistent<Savegame>>,
    mut gift_box_query: Query<
        (Entity, &Transform, &mut GiftboxComponent),
        (
            With<GiftboxComponent>,
            Without<GiftboxRays>,
            Without<GiftboxRaysLight>,
        ),
    >,
    gift_box_rays_query: Query<
        Entity,
        (
            With<GiftboxRays>,
            Without<GiftboxComponent>,
            Without<GiftboxRaysLight>,
        ),
    >,
    gift_box_rays_light_query: Query<
        Entity,
        (
            With<GiftboxRaysLight>,
            Without<GiftboxComponent>,
            Without<GiftboxRays>,
        ),
    >,
    gift_box_sound_query: Query<Entity, With<GiftboxOpenSound>>,
    model_assets: Res<ModelAssets>,
    sfx_assets: Res<SFXAssets>,
    font_assets: Res<FontAssets>,
    gui_assets: Res<GUIAssets>,
    mut mesh_assets: ResMut<Assets<Mesh>>,
    mut purchase_event_writer: EventWriter<PurchaseEvent>,
) {
    for (e, t, mut c) in gift_box_query.iter_mut() {
        if c.0.eq(&GiftboxStatus::Locked) {
            continue;
        }

        commands.spawn((
            AudioBundle {
                source: sfx_assets.chest_click.clone(),
                settings: PlaybackSettings {
                    mode: bevy::audio::PlaybackMode::Despawn,
                    ..default()
                },
            },
            GameObjectComponent,
        ));

        gift_box_rays_light_query.iter().for_each(|e| {
            commands.entity(e).despawn_recursive();
        });

        gift_box_rays_query.iter().for_each(|e| {
            commands.entity(e).despawn_recursive();
        });

        gift_box_sound_query.iter().for_each(|e| {
            commands.entity(e).despawn_recursive();
        });

        commands.entity(e).remove::<SceneBundle>();
        commands.entity(e).insert(SceneBundle {
            scene: model_assets.chest_prop.clone(),
            transform: t.clone(),
            ..default()
        });

        c.0 = GiftboxStatus::Locked;

        let mut rng = rand::thread_rng();

        let mut choice: u8;

        loop {
            choice = rng.gen::<u8>() % 3;

            if choice == 0 && savegame.pets.values().sum::<u32>() >= 1 {
                break;
            } else if choice == 1 && savegame.multiplier > 0.0 {
                break;
            } else if choice >= 2 {
                break;
            }
        }

        let value: f64;

        match choice {
            // Free pet
            0 => {
                let pets = savegame.pets.clone();
                let mut pets: Vec<(&String, &u32)> = pets.iter().map(|(k, v)| (k, v)).collect();

                pets.sort_by(|a, b| a.1.cmp(b.1));

                let pet = &pets[0];
                let mut amount = (*pet.1 as f32 * 0.25).trunc();

                if amount == 0.0 {
                    amount = 1.0;
                }

                savegame
                    .pets
                    .entry(pet.0.clone())
                    .and_modify(|x| *x += amount as u32);

                // activate pet generation
                purchase_event_writer.send(PurchaseEvent);

                value = amount as f64;
            }
            // Free multiplier
            1 => {
                let multiplier = savegame.multiplier * 0.1;
                savegame.multiplier += multiplier;
                value = multiplier;
            }
            // Free money
            _ => {
                let money = savegame.money * 0.1;
                savegame.money += money;
                value = money;
            }
        }

        commands.spawn((
            BillboardTextBundle {
                text: Text::from_section(
                    format!("+{:.1}", value),
                    get_text_style_default(&font_assets),
                ),
                transform: Transform::from_xyz(
                    t.translation.x,
                    t.translation.y + 3.5,
                    t.translation.z,
                )
                .with_scale(Vec3::splat(0.02)),
                ..default()
            },
            GiftboxNotificationText,
        ));

        commands.spawn((
            BillboardTextureBundle {
                texture: BillboardTextureHandle(match choice {
                    0 => gui_assets.pets.clone(),
                    1 => gui_assets.multiplier.clone(),
                    _ => gui_assets.money.clone(),
                }),
                mesh: BillboardMeshHandle(
                    mesh_assets.add(Rectangle::from_size(Vec2::new(10.0, 10.0))),
                ),
                transform: Transform::from_xyz(
                    t.translation.x,
                    t.translation.y + 3.0,
                    t.translation.z,
                )
                .with_scale(Vec3::splat(0.05)),
                ..default()
            },
            GiftboxNotificationIcon,
        ));

        break;
    }
}

pub fn spin_rays(
    time: Res<Time>,
    mut query: Query<(&mut Transform, &Visibility), With<GiftboxRays>>,
) {
    for (mut t, v) in query.iter_mut() {
        if *v == Visibility::Hidden {
            continue;
        }

        t.rotation = t.rotation * Quat::from_rotation_z(1.0 * time.delta_seconds());
    }
}

pub fn update_giftbox_notification_position(
    mut commands: Commands,
    mut text_query: Query<
        (Entity, &mut Transform),
        (
            With<GiftboxNotificationText>,
            Without<GiftboxNotificationIcon>,
        ),
    >,
    mut icon_query: Query<
        (Entity, &mut Transform),
        (
            With<GiftboxNotificationIcon>,
            Without<GiftboxNotificationText>,
        ),
    >,
    time: Res<Time>,
) {
    for (e, mut t) in text_query.iter_mut() {
        if t.translation.y >= 4.0 {
            commands.entity(e).despawn();
        }

        t.translation.y += 0.1 * time.delta_seconds();
    }

    for (e, mut t) in icon_query.iter_mut() {
        if t.translation.y >= 3.5 {
            commands.entity(e).despawn();
        }

        t.translation.y += 0.1 * time.delta_seconds();
    }
}
