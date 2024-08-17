use std::{f32::consts::PI, time::Duration};

use bevy::prelude::*;
use bevy_sprite3d::TextureAtlas3dData;
use bevy_tweening::{
    lens::{TransformRotationLens, TransformScaleLens},
    Animator, EaseFunction, RepeatCount, RepeatStrategy, Tracks, Tween,
};

#[derive(Component)]
pub struct AnimationTimer(pub Timer);

pub struct MaxonAnimationsPlugin;

impl Plugin for MaxonAnimationsPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            Update,
            (
                update_texture_atlases,
                update_3d_sprites,
                thug_shaker_animation,
            ),
        );
    }
}

fn update_texture_atlases(
    time: Res<Time>,
    mut query: Query<(&mut AnimationTimer, &mut TextureAtlas)>,
    texture_atlas_layout_assets: Res<Assets<TextureAtlasLayout>>,
) {
    for (mut timer, mut atlas) in query.iter_mut() {
        timer.0.tick(time.delta());

        if timer.0.just_finished() {
            if let Some(layout) = texture_atlas_layout_assets.get(atlas.layout.id()) {
                atlas.index = (atlas.index + 1) % layout.textures.len();
            }
        }
    }
}

fn update_3d_sprites(
    time: Res<Time>,
    mut query: Query<(&mut AnimationTimer, &mut TextureAtlas, &TextureAtlas3dData)>,
) {
    for (mut timer, mut atlas, data) in query.iter_mut() {
        timer.0.tick(time.delta());

        if timer.0.just_finished() {
            atlas.index = (atlas.index + 1) % data.keys.len();
        }
    }
}

#[derive(Component)]
pub struct ThugshakerAnimation;

fn thug_shaker_animation(
    mut commands: Commands,
    mut query: Query<
        (
            Entity,
            &Interaction,
            &mut Transform,
            Option<&Animator<Transform>>,
        ),
        With<ThugshakerAnimation>,
    >,
) {
    for (e, i, mut t, anim) in query.iter_mut() {
        match *i {
            Interaction::Hovered if anim.is_none() => {
                commands.entity(e).insert(Animator::new({
                    let scale = Tween::new(
                        EaseFunction::SineIn,
                        Duration::from_millis(500),
                        TransformScaleLens {
                            start: Vec3::new(0.9, 0.9, 0.0),
                            end: Vec3::new(1.0, 1.0, 0.0),
                        },
                    );

                    let thug_shaker = Tween::new(
                        EaseFunction::SineInOut,
                        Duration::from_millis(100),
                        TransformRotationLens {
                            start: Quat::from_rotation_z(-2.0 * PI / 180.0),
                            end: Quat::from_rotation_z(2.0 * PI / 180.0),
                        },
                    )
                    .with_repeat_count(RepeatCount::Infinite)
                    .with_repeat_strategy(RepeatStrategy::MirroredRepeat);

                    Tracks::new([scale, thug_shaker])
                }));
            }
            Interaction::None if anim.is_some() => {
                commands.entity(e).remove::<Animator<Transform>>();
            }
            Interaction::None => {
                t.scale = Vec3::splat(1.0);
                t.rotation = Quat::from_rotation_z(0.0);
            }
            _ => {}
        }
    }
}
