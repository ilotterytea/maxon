use std::f32::consts::PI;

use bevy::prelude::*;

use crate::ModelAssets;

#[derive(Component)]
pub struct GiftboxComponent(pub GiftboxStatus);

pub enum GiftboxStatus {
    Locked,
    Opened,
}

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
}
