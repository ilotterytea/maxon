use std::f32::consts::PI;

use bevy::{prelude::*, scene::SceneInstance};

use crate::ModelAssets;

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
        GiftboxComponent(GiftboxStatus::Locked),
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
) {
    gift_box_timer.0.tick(time.delta());

    if !gift_box_timer.0.just_finished() {
        return;
    }

    for (e, transform, mut comp) in gift_box_query.iter_mut() {
        if comp.0 == GiftboxStatus::Opened {
            continue;
        }

        commands.entity(e).remove::<SceneBundle>();
        commands.entity(e).insert(SceneBundle {
            scene: model_assets.chest_opened_prop.clone(),
            transform: transform.clone(),
            ..default()
        });

        comp.0 = GiftboxStatus::Opened;
    }
}
