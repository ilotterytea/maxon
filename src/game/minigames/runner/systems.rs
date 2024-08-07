use std::{f32::consts::PI, time::Duration};

use bevy::{
    math::bounding::{Aabb2d, IntersectsVolume},
    prelude::*,
};
use bevy_tweening::{
    lens::{TransformPositionLens, TransformRotateZLens, TransformScaleLens},
    Animator, Delay, EaseFunction, Sequence, Tracks, Tween,
};
use rand::{thread_rng, Rng};

use crate::{
    game::minigames::MinigameState,
    style::{get_text_style_minigame, get_text_style_minigame_large_value},
    AppState, FontAssets, GUIAssets, SpriteAssets,
};

#[derive(Component)]
pub(super) struct RunnerObjectComponent;

#[derive(Component)]
pub(super) struct GroundComponent;

#[derive(Component)]
pub(super) struct PlayerComponent {
    pub velocity: f32,
    pub jumped: bool,
    pub died: bool,
}

#[derive(Component)]
pub(super) struct ObstacleComponent {
    pub max_atlas_index: usize,
}

#[derive(Component)]
pub(super) struct UIHopTextComponent;

#[derive(Component)]
pub(super) struct UIHopSecondTextComponent;

#[derive(Component)]
pub(super) struct UIHopImageComponent;

#[derive(Component)]
pub(super) struct ScoreComponent;

#[derive(Resource)]
pub struct ScoreResource(pub u32);

pub(super) fn setup_runner(
    mut commands: Commands,
    sprite_assets: Res<SpriteAssets>,
    gui_assets: Res<GUIAssets>,
    font_assets: Res<FontAssets>,
) {
    let (width, height) = (800.0, 600.0);
    let (h_w, h_h) = (width / 2.0, height / 2.0);
    let padding = 60.0;

    commands.spawn((
        SpriteBundle {
            texture: sprite_assets.runner_ground.clone(),
            transform: Transform::from_xyz(0.0, -h_h + padding * 2.0, 0.0)
                .with_scale(Vec3::new(10.6, 0.92, 0.0)),
            ..default()
        },
        ImageScaleMode::Tiled {
            tile_x: true,
            tile_y: false,
            stretch_value: 0.1,
        },
        Name::new("Ground"),
        GroundComponent,
        RunnerObjectComponent,
    ));

    commands.spawn((
        SpriteBundle {
            sprite: Sprite {
                rect: Some(Rect::new(0.0, 0.0, 112.0, 112.0)),
                ..default()
            },
            texture: sprite_assets.runner_player.clone(),
            transform: Transform::from_xyz(-h_w + padding * 4.0, -h_h + padding * 3.0 + 17.0, 0.0)
                .with_scale(Vec3::splat(0.3)),
            ..default()
        },
        Name::new("Player"),
        PlayerComponent {
            velocity: 0.0,
            jumped: false,
            died: false,
        },
        RunnerObjectComponent,
    ));

    // - - -  U I  - - -
    commands.spawn((
        Text2dBundle {
            text: Text::from_section("- Hop", get_text_style_minigame(&font_assets)),
            transform: Transform::from_xyz(-150.0, -200.0, 1.0),
            ..default()
        },
        Name::new("Hop text"),
        UIHopTextComponent,
        RunnerObjectComponent,
    ));

    commands.spawn((
        Text2dBundle {
            text: Text::from_section("PRESS", get_text_style_minigame(&font_assets)),
            transform: Transform::from_xyz(-270.0, -200.0, 1.0),
            visibility: Visibility::Hidden,
            ..default()
        },
        Name::new("Hop second text"),
        UIHopSecondTextComponent,
        RunnerObjectComponent,
    ));

    commands.spawn((
        SpriteBundle {
            texture: gui_assets.key_space.clone(),
            transform: Transform::from_xyz(-260.0, -205.0, 1.0).with_scale(Vec3::splat(2.0)),
            ..default()
        },
        Name::new("Hop key"),
        UIHopImageComponent,
        RunnerObjectComponent,
    ));

    commands.spawn((
        Text2dBundle {
            text: Text::from_section("- Quit", get_text_style_minigame(&font_assets)),
            transform: Transform::from_xyz(-150.0, -155.0, 1.0),
            ..default()
        },
        Name::new("Quit text"),
        RunnerObjectComponent,
    ));

    commands.spawn((
        SpriteBundle {
            texture: gui_assets.key_f.clone(),
            transform: Transform::from_xyz(-210.0, -160.0, 1.0).with_scale(Vec3::splat(2.0)),
            ..default()
        },
        Name::new("Quit key"),
        RunnerObjectComponent,
    ));

    commands.spawn((
        Text2dBundle {
            text: Text::from_section("00000", get_text_style_minigame_large_value(&font_assets)),
            transform: Transform::from_xyz(180.0, -165.0, 1.0),
            ..default()
        },
        Name::new("Score text"),
        ScoreComponent,
        RunnerObjectComponent,
    ));

    commands.spawn((
        SpriteBundle {
            texture: sprite_assets.runner_background.clone(),
            transform: Transform::from_xyz(0.0, 61.0, -1.0).with_scale(Vec3::new(10.6, 0.95, 1.0)),
            ..default()
        },
        ImageScaleMode::Tiled {
            tile_x: true,
            tile_y: false,
            stretch_value: 0.1,
        },
        Name::new("Background"),
        RunnerObjectComponent,
    ));

    commands.insert_resource(ScoreResource(0));
}

pub(super) fn despawn_runner(
    mut commands: Commands,
    query: Query<Entity, With<RunnerObjectComponent>>,
) {
    for e in query.iter() {
        commands.entity(e).despawn_recursive();
    }

    commands.remove_resource::<ScoreResource>();
}

pub(super) fn jump_player(
    mut commands: Commands,
    button_input: Res<ButtonInput<KeyCode>>,
    mut player_query: Query<(Entity, &mut PlayerComponent), With<PlayerComponent>>,
) {
    if let Ok((e, mut p)) = player_query.get_single_mut() {
        if button_input.just_pressed(KeyCode::Space) && !p.jumped && !p.died {
            p.velocity = 500.0;
            p.jumped = true;

            commands.entity(e).insert(Animator::new({
                let squeeze = Tween::new(
                    EaseFunction::SineInOut,
                    Duration::from_millis(250),
                    TransformScaleLens {
                        start: Vec3::splat(0.3),
                        end: Vec3::new(0.2, 0.3, 1.0),
                    },
                );

                let stretch = Tween::new(
                    EaseFunction::SineInOut,
                    Duration::from_millis(250),
                    TransformScaleLens {
                        start: Vec3::new(0.2, 0.3, 1.0),
                        end: Vec3::new(0.2, 0.4, 1.0),
                    },
                );

                let back = Tween::new(
                    EaseFunction::SineInOut,
                    Duration::from_millis(250),
                    TransformScaleLens {
                        start: Vec3::new(0.2, 0.4, 1.0),
                        end: Vec3::splat(0.3),
                    },
                );

                Sequence::new([squeeze, stretch, back])
            }));
        }
    }
}

pub(super) fn gravity_system(
    time: Res<Time>,
    mut player_query: Query<(&mut Transform, &mut PlayerComponent), With<PlayerComponent>>,
) {
    for (mut t, mut p) in player_query.iter_mut() {
        if !p.jumped {
            continue;
        }

        p.velocity -= 1500.0 * time.delta_seconds();
        t.translation.y += p.velocity * time.delta_seconds();

        if t.translation.y <= -103.0 {
            t.translation.y = -103.0;
            p.velocity = 0.0;
            p.jumped = false;
        }
    }
}

pub(super) fn spawn_obstacles(
    mut commands: Commands,
    sprite_assets: Res<SpriteAssets>,
    query: Query<&Transform, With<ObstacleComponent>>,
    player_query: Query<&PlayerComponent, With<PlayerComponent>>,
) {
    if let Ok(p) = player_query.get_single() {
        if p.died {
            return;
        }
    }

    let mut last_x = 0.0;

    for t in query.iter() {
        if t.translation.x > last_x {
            last_x = t.translation.x;
        }
    }

    let difference = 356.0 - last_x;

    if difference < 300.0 {
        return;
    }

    let mut rng = thread_rng();

    if rng.gen::<u8>() % 50 != 0 {
        return;
    }

    let scale = rng.gen_range(1.5..2.0);

    commands.spawn((
        SpriteBundle {
            texture: sprite_assets.runner_piston_texture.clone(),
            transform: Transform::from_xyz(356.0, -102.0 + scale * 9.0, 0.0)
                .with_scale(Vec3::splat(scale)),
            ..default()
        },
        TextureAtlas {
            layout: sprite_assets.runner_piston_layout.clone(),
            index: 0,
        },
        ObstacleComponent {
            max_atlas_index: rng.gen_range(0..12),
        },
        RunnerObjectComponent,
    ));
}

pub(super) fn update_obstacles(
    mut commands: Commands,
    time: Res<Time>,
    mut query: Query<
        (
            Entity,
            &mut Transform,
            &mut TextureAtlas,
            &ObstacleComponent,
        ),
        With<ObstacleComponent>,
    >,
) {
    for (e, mut t, mut a, c) in query.iter_mut() {
        t.translation.x -= 500.0 * time.delta_seconds();

        if t.translation.x <= 250.0 && a.index < c.max_atlas_index {
            a.index += 1;
        }

        if t.translation.x <= -356.0 {
            commands.entity(e).despawn_recursive();
        }
    }
}

#[allow(clippy::type_complexity)]
pub(super) fn check_obstacle_collision(
    mut commands: Commands,
    mut player_query: Query<(Entity, &Transform, &mut PlayerComponent), With<PlayerComponent>>,
    obstacle_query: Query<(Entity, &Transform), With<ObstacleComponent>>,
    mut hop_text_query: Query<
        (&mut Transform, &mut Text),
        (
            With<UIHopTextComponent>,
            Without<ObstacleComponent>,
            Without<PlayerComponent>,
            Without<UIHopImageComponent>,
        ),
    >,
    mut hop_second_text_query: Query<&mut Visibility, With<UIHopSecondTextComponent>>,
    gui_assets: Res<GUIAssets>,
    mut hop_sprite_query: Query<
        (&mut Transform, &mut Handle<Image>),
        (
            With<UIHopImageComponent>,
            Without<ObstacleComponent>,
            Without<UIHopTextComponent>,
            Without<PlayerComponent>,
        ),
    >,
) {
    if let Ok((p_e, p_t, mut p_c)) = player_query.get_single_mut() {
        if p_c.died {
            return;
        }

        let p_coll = Aabb2d::new(p_t.translation.truncate(), p_t.scale.truncate() / 2.0);
        for (_, o_t) in obstacle_query.iter() {
            let o_coll = Aabb2d::new(o_t.translation.truncate(), Vec2::new(16.0, 42.0));

            if p_coll.intersects(&o_coll) {
                commands.entity(p_e).insert(Animator::new({
                    let scale = Tween::new(
                        EaseFunction::SineInOut,
                        Duration::from_millis(500),
                        TransformScaleLens {
                            start: Vec3::splat(0.3),
                            end: Vec3::splat(10.0),
                        },
                    );

                    let rotation = Tween::new(
                        EaseFunction::SineInOut,
                        Duration::from_millis(500),
                        TransformRotateZLens {
                            start: 0.0,
                            end: 400.0 * PI / 180.0,
                        },
                    );

                    let position = Tween::new(
                        EaseFunction::SineInOut,
                        Duration::from_millis(500),
                        TransformPositionLens {
                            start: Vec3::new(-160.0, -3.0, 2.0),
                            end: Vec3::new(0.0, 0.0, 2.0),
                        },
                    );

                    let position_2 = Tween::new(
                        EaseFunction::SineInOut,
                        Duration::from_secs(1),
                        TransformPositionLens {
                            start: Vec3::new(0.0, 0.0, 2.0),
                            end: Vec3::new(0.0, -2000.0, 2.0),
                        },
                    );

                    Sequence::new([Tracks::new([scale, position, rotation])])
                        .then(Delay::new(Duration::from_secs(1)))
                        .then(position_2)
                }));

                obstacle_query.iter().for_each(|(e, _)| {
                    commands.entity(e).despawn_recursive();
                });

                p_c.died = true;

                // туториал как прыгать
                // дизайн
                // добавить посложнее уровни (чтобы после 1000 поинтов летал трезубец сверху)
                // эффект телика

                hop_text_query.iter_mut().for_each(|(mut t, mut text)| {
                    text.sections[0].value = "TO RESTART".into();
                    t.translation = Vec3::new(-111.0, -200.0, 1.0);
                });

                hop_second_text_query.iter_mut().for_each(|mut v| {
                    *v = Visibility::Visible;
                });

                hop_sprite_query.iter_mut().for_each(|(mut t, mut handle)| {
                    *handle = gui_assets.key_r.clone();
                    t.translation = Vec3::new(-210.0, -200.0, 1.0);
                });

                break;
            }
        }
    }
}

pub(super) fn update_score(
    mut score: ResMut<ScoreResource>,
    player_query: Query<&PlayerComponent, With<PlayerComponent>>,
    mut text_query: Query<&mut Text, With<ScoreComponent>>,
    time: Res<Time>,
) {
    if let Ok(p) = player_query.get_single() {
        if p.died {
            return;
        }

        score.0 += (time.delta_seconds() * 60.0) as u32;

        for mut text in text_query.iter_mut() {
            let mut score_text = String::with_capacity(5);
            let real_score_text = score.0.to_string();
            let zeros = 5 - real_score_text.len();

            for _ in 0..zeros {
                score_text.push('0');
            }

            score_text.push_str(&real_score_text);

            text.sections[0].value = score_text;
        }
    }
}

#[allow(clippy::too_many_arguments, clippy::type_complexity)]
pub(super) fn listen_keyboard_events(
    mut commands: Commands,
    keyboard_input: Res<ButtonInput<KeyCode>>,
    mut player_query: Query<(Entity, &mut Transform, &mut PlayerComponent), With<PlayerComponent>>,
    mut hop_text_query: Query<
        (&mut Transform, &mut Text),
        (
            With<UIHopTextComponent>,
            Without<PlayerComponent>,
            Without<UIHopImageComponent>,
        ),
    >,
    mut hop_second_text_query: Query<&mut Visibility, With<UIHopSecondTextComponent>>,
    gui_assets: Res<GUIAssets>,
    mut hop_sprite_query: Query<
        (&mut Transform, &mut Handle<Image>),
        (
            With<UIHopImageComponent>,
            Without<PlayerComponent>,
            Without<UIHopTextComponent>,
        ),
    >,
    mut score: ResMut<ScoreResource>,
    mut app_state: ResMut<NextState<AppState>>,
    mut minigame_state: ResMut<NextState<MinigameState>>,
) {
    if let Ok((e, mut t, mut p)) = player_query.get_single_mut() {
        if keyboard_input.just_pressed(KeyCode::KeyR) && p.died {
            p.died = false;
            commands.entity(e).remove::<Animator<Transform>>();

            t.translation = Vec3::new(-160.0, -103.0, 2.0);
            t.scale = Vec3::splat(0.3);
            t.rotation = Quat::default();

            hop_text_query.iter_mut().for_each(|(mut t, mut text)| {
                text.sections[0].value = " - Hop".into();
                t.translation = Vec3::new(-150.0, -200.0, 1.0);
            });

            hop_second_text_query.iter_mut().for_each(|mut v| {
                *v = Visibility::Hidden;
            });

            hop_sprite_query.iter_mut().for_each(|(mut t, mut handle)| {
                *handle = gui_assets.key_space.clone();
                t.translation = Vec3::new(-260.0, -205.0, 1.0);
            });

            score.0 = 0;
        }
    }

    if keyboard_input.just_pressed(KeyCode::KeyF) {
        app_state.set(AppState::MinigamesLobby);
        minigame_state.set(MinigameState::None);
    }
}
