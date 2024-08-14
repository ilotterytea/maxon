use bevy::{prelude::*, sprite::Anchor};
use bevy_persistent::Persistent;
use rand::{thread_rng, Rng};

use crate::{
    persistent::Savegame, style::get_text_style_minigame, AppState, FontAssets, GUIAssets,
    SFXAssets, SpriteAssets,
};

use super::MinigameState;

pub struct SlotsMinigamePlugin;

impl Plugin for SlotsMinigamePlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(MinigameState::Slots), setup_slots)
            .add_systems(
                Update,
                (update_slots, listen_for_keyboard, give_prize)
                    .run_if(in_state(MinigameState::Slots)),
            )
            .add_systems(OnExit(MinigameState::Slots), despawn_slots);
    }
}

#[derive(Component)]
struct SlotsObjectComponent;

#[derive(Clone, PartialEq, Eq, PartialOrd, Ord)]
enum SlotIcon {
    Arbuz,
    Icecream,
    Kochan,
    Buter,
    Corn,
    Kebab,
    Onions,
    Treat,
}

impl SlotIcon {
    pub fn get_multiplier(&self) -> f64 {
        match self {
            Self::Arbuz => 5.0,
            Self::Icecream => 30.0,
            Self::Kochan => 80.0,
            Self::Buter => 120.0,
            Self::Corn => 200.0,
            Self::Kebab => 500.0,
            Self::Onions => 1000.0,
            Self::Treat => 2500.0,
        }
    }

    pub fn get_image(&self, sprite_assets: &Res<SpriteAssets>) -> Handle<Image> {
        match self {
            Self::Arbuz => &sprite_assets.slots_arbuz,
            Self::Icecream => &sprite_assets.slots_icecream,
            Self::Kochan => &sprite_assets.slots_kochan,
            Self::Buter => &sprite_assets.slots_buterbror,
            Self::Corn => &sprite_assets.slots_corn,
            Self::Kebab => &sprite_assets.slots_kebab,
            Self::Onions => &sprite_assets.slots_onions,
            Self::Treat => &sprite_assets.slots_treat,
        }
        .clone()
    }

    pub fn as_usize(&self) -> usize {
        match self {
            Self::Arbuz => 0,
            Self::Icecream => 1,
            Self::Kochan => 2,
            Self::Buter => 3,
            Self::Corn => 4,
            Self::Kebab => 5,
            Self::Onions => 6,
            Self::Treat => 7,
        }
    }

    pub fn as_slice() -> &'static [Self; 8] {
        &[
            Self::Arbuz,
            Self::Icecream,
            Self::Kochan,
            Self::Buter,
            Self::Corn,
            Self::Kebab,
            Self::Onions,
            Self::Treat,
        ]
    }
}

#[derive(Component)]
struct SlotsItem {
    pub num: u8,
    pub column: u8,
    pub stop: bool,
    pub selected: bool,
    pub icon: SlotIcon,
}

impl Default for SlotsItem {
    fn default() -> Self {
        Self {
            num: 1,
            column: 1,
            stop: false,
            selected: false,
            icon: SlotIcon::Arbuz,
        }
    }
}

#[derive(Resource)]
pub struct SlotsResource {
    pub spinned: bool,
    pub prize_given: bool,
    pub locked: bool,
    pub stake: f64,
    pub stake_percent: f64,
    pub timer: Timer,
    pub quit_attempts: u8,
    pub free_spins: u8,
}

#[derive(Component)]
struct SlotsStakeText;

#[derive(Component)]
struct SlotsMoneyText;

#[derive(Component)]
struct SlotsPrizeText;

#[derive(Component)]
struct SlotsPrizeIcon;

#[derive(Component)]
struct SlotsPlay;

#[derive(Component)]
struct SlotsQuitText;

#[derive(Component)]
struct SlotsMusic;

fn setup_slots(
    mut commands: Commands,
    savegame: Res<Persistent<Savegame>>,
    sprite_assets: Res<SpriteAssets>,
    gui_assets: Res<GUIAssets>,
    font_assets: Res<FontAssets>,
) {
    let mut slots_resource = SlotsResource {
        spinned: true,
        prize_given: true,
        locked: false,
        stake: 0.0,
        stake_percent: 1.0,
        timer: Timer::from_seconds(2.0, TimerMode::Once),
        quit_attempts: if savegame.minigames.slots.free_spins_acquired {
            5
        } else {
            0
        },
        free_spins: 0,
    };

    slots_resource.stake = (slots_resource.stake_percent / 100.0) * savegame.money;

    slots_resource.locked =
        slots_resource.stake <= 0.0 || savegame.money - slots_resource.stake < 0.0;

    commands.spawn((
        SpriteBundle {
            texture: sprite_assets.slots_background.clone(),
            ..default()
        },
        Name::new("Background"),
        SlotsObjectComponent,
    ));

    commands.spawn((
        SpriteBundle {
            sprite: Sprite {
                color: Srgba::rgb_u8(255, 254, 219).into(),
                ..default()
            },
            transform: Transform::from_xyz(-125.0, 0.0, -2.0)
                .with_scale(Vec3::new(400.0, 120.0, 0.0)),
            ..default()
        },
        Name::new("Background 2"),
        SlotsObjectComponent,
    ));

    let mut rng = thread_rng();
    let icons = SlotIcon::as_slice();

    // Creating slot icons
    for i in 0..=2 {
        for j in 0..2 {
            let icon = icons[rng.r#gen::<usize>() % icons.len()].clone();
            commands.spawn((
                SpriteBundle {
                    transform: Transform::from_xyz(
                        // -124 17
                        -267.0 + 143.0 * i as f32,
                        110.0 * j as f32,
                        -1.0,
                    ),
                    texture: icon.get_image(&sprite_assets),
                    ..default()
                },
                Name::new(format!("Slot icon {} column {}", j + 1, i + 1)),
                SlotsObjectComponent,
                SlotsItem {
                    num: j + 1,
                    column: i + 1,
                    icon,
                    ..default()
                },
            ));
        }
    }

    // Prize text
    commands.spawn((
        Text2dBundle {
            text: Text::from_section("YOU WON NOTHINGG", get_text_style_minigame(&font_assets)),
            transform: Transform::from_xyz(-50.0, 90.0, 1.0),
            text_anchor: Anchor::CenterRight,
            visibility: Visibility::Hidden,
            ..default()
        },
        SlotsPrizeText,
        SlotsObjectComponent,
        Name::new("Slots prize text"),
    ));

    // Prize icon
    commands.spawn((
        SpriteBundle {
            texture: gui_assets.money.clone(),
            transform: Transform::from_xyz(-30.0, 90.0, 1.0).with_scale(Vec3::splat(0.5)),
            visibility: Visibility::Hidden,
            ..default()
        },
        SlotsPrizeIcon,
        SlotsObjectComponent,
        Name::new("Slots prize icon"),
    ));

    // Stake control
    commands.spawn((
        SpriteBundle {
            texture: gui_assets.key_d.clone(),
            transform: Transform::from_xyz(60.0, -155.0, 1.0).with_scale(Vec3::splat(2.0)),
            ..default()
        },
        SlotsObjectComponent,
        Name::new("Key D"),
    ));

    commands.spawn((
        Text2dBundle {
            text: Text::from_section("+1%", get_text_style_minigame(&font_assets)),
            transform: Transform::from_xyz(20.0, -150.0, 1.0),
            ..default()
        },
        SlotsObjectComponent,
        Name::new("+1 percent"),
    ));

    commands.spawn((
        SpriteBundle {
            texture: gui_assets.key_a.clone(),
            transform: Transform::from_xyz(60.0, -195.0, 1.0).with_scale(Vec3::splat(2.0)),
            ..default()
        },
        SlotsObjectComponent,
        Name::new("Key A"),
    ));

    commands.spawn((
        Text2dBundle {
            text: Text::from_section("-1%", get_text_style_minigame(&font_assets)),
            transform: Transform::from_xyz(20.0, -190.0, 1.0),
            ..default()
        },
        SlotsObjectComponent,
        Name::new("-1 percent"),
    ));

    // Spin
    commands.spawn((
        SpriteBundle {
            texture: gui_assets.key_space.clone(),
            transform: Transform::from_xyz(-265.0, -140.0, 1.0).with_scale(Vec3::splat(2.0)),
            visibility: if slots_resource.locked {
                Visibility::Hidden
            } else {
                Visibility::Visible
            },
            ..default()
        },
        SlotsObjectComponent,
        Name::new("Key space"),
    ));

    commands.spawn((
        Text2dBundle {
            text: Text::from_section("- Spin", get_text_style_minigame(&font_assets)),
            transform: Transform::from_xyz(-185.0, -135.0, 1.0),
            text_anchor: Anchor::CenterLeft,
            visibility: if slots_resource.locked {
                Visibility::Hidden
            } else {
                Visibility::Visible
            },
            ..default()
        },
        SlotsObjectComponent,
        Name::new("Spin text"),
    ));

    // Quit
    commands.spawn((
        SpriteBundle {
            texture: gui_assets.key_f.clone(),
            transform: Transform::from_xyz(-215.0, -180.0, 1.0).with_scale(Vec3::splat(2.0)),
            ..default()
        },
        SlotsObjectComponent,
        Name::new("Key F"),
    ));

    commands.spawn((
        Text2dBundle {
            text: Text::from_section("- Quit", get_text_style_minigame(&font_assets)),
            text_anchor: Anchor::CenterLeft,
            transform: Transform::from_xyz(-185.0, -180.0, 1.0),
            ..default()
        },
        SlotsObjectComponent,
        SlotsQuitText,
        Name::new("Quit text"),
    ));

    // Stake
    commands.spawn((
        Text2dBundle {
            text: Text::from_section(
                format!(
                    "{} ({}%)",
                    slots_resource.stake.trunc(),
                    slots_resource.stake_percent
                ),
                get_text_style_minigame(&font_assets),
            ),
            transform: Transform::from_xyz(40.0, -115.0, 1.0),
            text_anchor: Anchor::CenterRight,
            ..default()
        },
        SlotsStakeText,
        SlotsObjectComponent,
        Name::new("Stake text"),
    ));

    // Stake icon
    commands.spawn((
        SpriteBundle {
            texture: gui_assets.money.clone(),
            transform: Transform::from_xyz(60.0, -118.0, 1.0).with_scale(Vec3::splat(0.5)),
            ..default()
        },
        SlotsObjectComponent,
        Name::new("Stake icon"),
    ));

    // Money
    commands.spawn((
        Text2dBundle {
            text: Text::from_section(
                savegame.money.trunc().to_string(),
                get_text_style_minigame(&font_assets),
            ),
            transform: Transform::from_xyz(40.0, -80.0, 1.0),
            text_anchor: Anchor::CenterRight,
            ..default()
        },
        SlotsMoneyText,
        SlotsObjectComponent,
        Name::new("Money text"),
    ));

    // Money icon
    commands.spawn((
        SpriteBundle {
            texture: gui_assets.money.clone(),
            transform: Transform::from_xyz(60.0, -85.0, 1.0).with_scale(Vec3::splat(0.5)),
            ..default()
        },
        SlotsObjectComponent,
        Name::new("Money icon"),
    ));

    commands.insert_resource(slots_resource);
}

fn update_slots(
    mut commands: Commands,
    time: Res<Time>,
    sprite_assets: Res<SpriteAssets>,
    sfx_assets: Res<SFXAssets>,
    mut slots_resource: ResMut<SlotsResource>,
    mut query: Query<(&mut SlotsItem, &mut Transform, &mut Handle<Image>), With<SlotsItem>>,
) {
    if slots_resource.spinned {
        return;
    }

    slots_resource.timer.tick(time.delta());

    let mut rng = thread_rng();
    let mut icons = &SlotIcon::as_slice()[..];

    if slots_resource.free_spins > 0 {
        icons = &SlotIcon::as_slice()[0..1];
    }

    let stop = rng.r#gen::<u8>() % 12 == 2 && slots_resource.timer.elapsed_secs() >= 2.0;

    let (mut col, mut set) = (0, false);

    for (mut i, mut t, mut h) in query.iter_mut() {
        if i.stop {
            continue;
        }

        if stop && !set && t.translation.y > -10.0 && t.translation.y <= 10.0 {
            col = i.column;
            set = true;

            commands.spawn((
                AudioBundle {
                    source: sfx_assets.slots_column_selected.clone(),
                    settings: PlaybackSettings {
                        mode: bevy::audio::PlaybackMode::Despawn,
                        ..default()
                    },
                },
                SlotsObjectComponent,
            ));

            continue;
        }

        t.translation.y -= 1000.0 * time.delta_seconds();

        if t.translation.y <= -120.0 {
            t.translation.y = 110.0;
            i.icon = icons[rng.r#gen::<usize>() % icons.len()].clone();
            *h = i.icon.get_image(&sprite_assets);
        }
    }

    if stop {
        query.iter_mut().for_each(|(mut i, mut t, _)| {
            if !i.stop && i.column == col {
                i.stop = true;

                t.translation.y = if i.num == 1 {
                    i.selected = true;
                    0.0
                } else {
                    110.0
                };
            }
        });

        slots_resource.spinned = query.iter().all(|(i, _, _)| i.stop);
    }
}

#[allow(clippy::too_many_arguments, clippy::type_complexity)]
fn give_prize(
    mut commands: Commands,
    mut slots_resource: ResMut<SlotsResource>,
    mut savegame: ResMut<Persistent<Savegame>>,
    sfx_assets: Res<SFXAssets>,
    query: Query<&SlotsItem>,
    mut prize_text_query: Query<
        &mut Text,
        (
            With<SlotsPrizeText>,
            Without<SlotsStakeText>,
            Without<SlotsMoneyText>,
        ),
    >,
    mut prize_query: Query<
        &mut Visibility,
        (
            Or<(With<SlotsPrizeText>, With<SlotsPrizeIcon>)>,
            Without<SlotsPlay>,
        ),
    >,
    mut play_query: Query<
        &mut Visibility,
        (
            Without<SlotsPrizeText>,
            Without<SlotsPrizeIcon>,
            With<SlotsPlay>,
        ),
    >,
    mut stake_query: Query<
        &mut Text,
        (
            With<SlotsStakeText>,
            Without<SlotsPrizeText>,
            Without<SlotsMoneyText>,
        ),
    >,
    mut money_query: Query<
        &mut Text,
        (
            With<SlotsMoneyText>,
            Without<SlotsPrizeText>,
            Without<SlotsStakeText>,
        ),
    >,
    music_query: Query<Entity, With<SlotsMusic>>,
) {
    if !slots_resource.spinned || slots_resource.prize_given {
        return;
    }

    music_query
        .iter()
        .for_each(|e| commands.entity(e).despawn_recursive());

    slots_resource.timer = Timer::from_seconds(2.0, TimerMode::Once);

    let items = query
        .iter()
        .filter(|x| x.selected)
        .collect::<Vec<&SlotsItem>>();
    let mut prev_item = &items[0];
    let mut all_same = false;

    for item in items.iter() {
        all_same = item.icon.eq(&prev_item.icon);

        if !all_same {
            break;
        }

        prev_item = item;
    }

    let mut prize: f64 = if all_same {
        slots_resource.stake * prev_item.icon.get_multiplier()
    } else {
        -slots_resource.stake
    };

    if prize.is_sign_negative() {
        savegame.minigames.slots.total_spins += 1;
    } else {
        savegame.minigames.slots.total_spins += 1;
        savegame.minigames.slots.wins += 1;
    }

    if slots_resource.free_spins > 0 {
        slots_resource.free_spins -= 1;

        if prize.is_sign_negative() {
            prize *= -1.0;
        }
    }

    let text = if prize > 0.0 {
        format!("YOU WON {}", prize.trunc())
    } else {
        "YOU WON NOTHINGG".into()
    };

    prize_query
        .iter_mut()
        .for_each(|mut x| *x = Visibility::Visible);

    prize_text_query
        .iter_mut()
        .for_each(|mut x| x.sections[0].value.clone_from(&text));

    slots_resource.prize_given = true;

    savegame.money += prize;
    savegame.persist().expect("Failed to save");

    slots_resource.stake = (slots_resource.stake_percent / 100.0) * savegame.money;
    slots_resource.locked =
        slots_resource.stake <= 0.0 || savegame.money - slots_resource.stake < 0.0;

    if slots_resource.locked {
        play_query
            .iter_mut()
            .for_each(|mut v| *v = Visibility::Hidden);
    }

    stake_query.iter_mut().for_each(|mut text| {
        text.sections[0].value = format!(
            "{} ({}%)",
            slots_resource.stake.trunc(),
            slots_resource.stake_percent
        );
    });
    money_query.iter_mut().for_each(|mut text| {
        text.sections[0].value = savegame.money.trunc().to_string();
    });

    let handle = if prize > 0.0 {
        let usize_id = prev_item.icon.as_usize();

        if (0..3).contains(&usize_id) {
            &sfx_assets.slots_small_win
        } else if (3..7).contains(&usize_id) {
            &sfx_assets.slots_medium_win
        } else {
            &sfx_assets.slots_big_win
        }
    } else {
        &sfx_assets.slots_fail
    }
    .clone();

    commands.spawn((
        AudioBundle {
            source: handle,
            settings: PlaybackSettings {
                mode: bevy::audio::PlaybackMode::Despawn,
                ..default()
            },
        },
        SlotsObjectComponent,
    ));
}

fn despawn_slots(mut commands: Commands, query: Query<Entity, With<SlotsObjectComponent>>) {
    for e in query.iter() {
        commands.entity(e).despawn_recursive();
    }

    commands.remove_resource::<SlotsResource>();
}

#[allow(clippy::too_many_arguments, clippy::type_complexity)]
fn listen_for_keyboard(
    mut commands: Commands,
    sfx_assets: Res<SFXAssets>,
    mut savegame: ResMut<Persistent<Savegame>>,
    keyboard_input: Res<ButtonInput<KeyCode>>,
    mut slots_resource: ResMut<SlotsResource>,
    mut item_query: Query<&mut SlotsItem>,
    mut prize_query: Query<&mut Visibility, Or<(With<SlotsPrizeText>, With<SlotsPrizeIcon>)>>,
    mut stake_query: Query<&mut Text, (With<SlotsStakeText>, Without<SlotsQuitText>)>,
    mut quit_query: Query<&mut Text, (With<SlotsQuitText>, Without<SlotsStakeText>)>,
    mut app_state: ResMut<NextState<AppState>>,
    mut minigame_state: ResMut<NextState<MinigameState>>,
) {
    if slots_resource.spinned {
        if keyboard_input.just_pressed(KeyCode::Space) && !slots_resource.locked {
            item_query.iter_mut().for_each(|mut x| {
                x.stop = false;
                x.selected = false;
            });
            slots_resource.spinned = false;
            slots_resource.prize_given = false;

            prize_query
                .iter_mut()
                .for_each(|mut x| *x = Visibility::Hidden);

            commands.spawn((
                AudioBundle {
                    source: sfx_assets.slots_start.clone(),
                    settings: PlaybackSettings {
                        mode: bevy::audio::PlaybackMode::Despawn,
                        ..default()
                    },
                },
                SlotsObjectComponent,
            ));

            commands.spawn((
                AudioBundle {
                    source: sfx_assets.slots_loop.clone(),
                    settings: PlaybackSettings {
                        mode: bevy::audio::PlaybackMode::Loop,
                        ..default()
                    },
                },
                SlotsMusic,
                SlotsObjectComponent,
            ));
        }

        if keyboard_input.just_pressed(KeyCode::KeyA)
            && slots_resource.stake_percent > 1.0
            && !slots_resource.locked
            && slots_resource.free_spins == 0
        {
            slots_resource.stake_percent -= 1.0;
            slots_resource.stake = (slots_resource.stake_percent / 100.0) * savegame.money;

            stake_query.iter_mut().for_each(|mut text| {
                text.sections[0].value = format!(
                    "{} ({}%)",
                    slots_resource.stake.trunc(),
                    slots_resource.stake_percent
                );
            });
        }

        if keyboard_input.just_pressed(KeyCode::KeyD)
            && slots_resource.stake_percent < 100.0
            && !slots_resource.locked
            && slots_resource.free_spins == 0
        {
            slots_resource.stake_percent += 1.0;
            slots_resource.stake = (slots_resource.stake_percent / 100.0) * savegame.money;

            stake_query.iter_mut().for_each(|mut text| {
                text.sections[0].value = format!(
                    "{} ({}%)",
                    slots_resource.stake.trunc(),
                    slots_resource.stake_percent
                );
            });
        }

        if keyboard_input.just_pressed(KeyCode::KeyF) {
            let value: String = match slots_resource.quit_attempts {
                // NEXT SPIN IS FREE
                1 => {
                    slots_resource.free_spins = 1;
                    slots_resource.stake_percent = 1.0;
                    slots_resource.stake = (slots_resource.stake_percent / 100.0) * savegame.money;

                    stake_query.iter_mut().for_each(|mut text| {
                        text.sections[0].value = format!(
                            "{} ({}%)",
                            slots_resource.stake.trunc(),
                            slots_resource.stake_percent
                        );
                    });
                    "NEXT SPIN IS FREE".into()
                }
                // FREE SPIN BEFORE QUIT
                2 => {
                    slots_resource.free_spins = 1;
                    slots_resource.stake_percent = 1.0;
                    slots_resource.stake = (slots_resource.stake_percent / 100.0) * savegame.money;

                    stake_query.iter_mut().for_each(|mut text| {
                        text.sections[0].value = format!(
                            "{} ({}%)",
                            slots_resource.stake.trunc(),
                            slots_resource.stake_percent
                        );
                    });
                    "FREE SPIN BEFORE QUIT".into()
                }
                // 5 FREE SPINS BEFORE QUIT
                3 => {
                    slots_resource.free_spins = 5;
                    slots_resource.stake_percent = 1.0;
                    slots_resource.stake = (slots_resource.stake_percent / 100.0) * savegame.money;

                    stake_query.iter_mut().for_each(|mut text| {
                        text.sections[0].value = format!(
                            "{} ({}%)",
                            slots_resource.stake.trunc(),
                            slots_resource.stake_percent
                        );
                    });
                    "5 FREE SPINS BEFORE QUIT".into()
                }
                // 99% OF GAMBLERS QUIT BEFORE THEIR BIG WIN
                0 | 4 => "99% OF GAMBLERS QUIT BEFORE THEIR BIG WIN".into(),
                _ => {
                    app_state.set(AppState::MinigamesLobby);
                    minigame_state.set(MinigameState::None);
                    savegame.minigames.slots.free_spins_acquired = true;
                    savegame.persist().expect("Failed to save");
                    "- QUIT".into()
                }
            };

            quit_query
                .iter_mut()
                .for_each(|mut text| text.sections[0].value.clone_from(&value));

            slots_resource.quit_attempts += 1;
        }
    }
}
