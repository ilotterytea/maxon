use bevy::{color::palettes::css as color, prelude::*};
use bevy_persistent::Persistent;
use bevy_simple_scroll_view::{ScrollView, ScrollableContent};

use crate::{
    animation::AnimationTimer,
    assets::FontAssets,
    game::components::GameObjectComponent,
    localization::{LineId, LocalizationManager},
    persistent::Savegame,
    style::*,
    DataAssets, GUIAssets, SFXAssets,
};

use super::{
    pets::{PetIdComponent, Pets},
    systems::PurchaseEvent,
    ShopMode, ShopMultiplier, ShopSettings,
};

#[derive(Component, PartialEq, Eq, Clone, Copy)]
pub enum PlayerStatsTextComponent {
    Money,
    Multiplier,
}

#[derive(Component)]
pub struct ShopUIListComponent;

#[derive(Component)]
pub struct ShopUIControlComponent;

#[derive(Component)]
pub struct ShopUIListToggleComponent;

#[derive(Component, PartialEq, Eq)]
pub enum PetComponent {
    Base,
    Icon,
    Name,
    Price,
    Amount,
}

#[derive(Component)]
pub struct PetDisabledComponent;

pub fn setup_ui(
    mut commands: Commands,
    font_assets: Res<FontAssets>,
    gui_assets: Res<GUIAssets>,
    data_assets: Res<DataAssets>,
    pets_assets: Res<Assets<Pets>>,
    savegame: Res<Persistent<Savegame>>,
    localization: Res<LocalizationManager>,
) {
    let pets = pets_assets
        .get(data_assets.pets.id())
        .expect("Failed to get pets");

    let pets = &pets.0;

    // Creating pet buttons
    let mut pet_ids: Vec<Entity> = Vec::new();

    for pet in pets {
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

        let id = commands
            .spawn((
                ButtonBundle {
                    style: Style {
                        display: Display::Flex,
                        flex_direction: FlexDirection::Row,
                        margin: UiRect::bottom(Val::Percent(1.0)),
                        padding: UiRect::all(Val::Percent(1.5)),
                        ..default()
                    },
                    background_color: STORE_ITEM_BG_COLOR.into(),
                    ..default()
                },
                Name::new(pet.id.clone()),
                PetIdComponent(pet.id.clone()),
                PetComponent::Base,
            ))
            .with_children(|parent| {
                // Icon
                parent.spawn((
                    ImageBundle {
                        style: Style {
                            width: Val::Px(48.0),
                            height: Val::Px(48.0),
                            ..default()
                        },
                        image: UiImage::new(icon.unwrap()),
                        ..default()
                    },
                    TextureAtlas::from(gui_assets.pet_icon_layout.clone()),
                    Name::new(format!("{} pet icon", pet.id)),
                    AnimationTimer(Timer::from_seconds(0.1, TimerMode::Repeating)),
                    PetIdComponent(pet.id.clone()),
                    PetComponent::Icon,
                ));

                // Summary
                parent
                    .spawn((
                        NodeBundle {
                            style: Style {
                                flex_grow: 1.0,
                                display: Display::Flex,
                                flex_direction: FlexDirection::Column,
                                margin: UiRect::left(Val::Percent(2.0)),
                                ..default()
                            },
                            ..default()
                        },
                        Name::new(format!("{} summary", pet.id)),
                    ))
                    .with_children(|sum| {
                        let pet_name_id = format!("\"pet.{}.name\"", pet.id);
                        let pet_name_id: LineId =
                            serde_json::from_str::<LineId>(pet_name_id.as_str()).unwrap();
                        // Name
                        sum.spawn((
                            TextBundle::from_section(
                                localization.get(pet_name_id),
                                get_text_style_default(&font_assets),
                            ),
                            Name::new(format!("{} text name", pet.id)),
                            PetIdComponent(pet.id.clone()),
                            PetComponent::Name,
                        ));

                        // Price
                        sum.spawn((
                            NodeBundle {
                                style: Style {
                                    width: Val::Percent(100.0),
                                    display: Display::Flex,
                                    flex_direction: FlexDirection::Row,
                                    ..default()
                                },
                                ..default()
                            },
                            Name::new(format!("{} price", pet.id)),
                        ))
                        .with_children(|price_node| {
                            let text_style = get_text_style_default(&font_assets);

                            price_node.spawn((
                                ImageBundle {
                                    image: UiImage::new(gui_assets.money.clone()),
                                    style: Style {
                                        margin: UiRect::right(Val::Percent(1.0)),
                                        width: Val::Px(text_style.font_size),
                                        height: Val::Px(text_style.font_size),
                                        ..default()
                                    },
                                    ..default()
                                },
                                Name::new(format!("{} price icon", pet.id)),
                            ));

                            price_node.spawn((
                                TextBundle::from_section(pet.price.to_string(), {
                                    let mut style = text_style.clone();
                                    style.color = color::LIME.into();
                                    style
                                }),
                                Name::new(format!("{} price text", pet.id)),
                                PetIdComponent(pet.id.clone()),
                                PetComponent::Price,
                            ));
                        });
                    });

                // Amount
                parent.spawn((
                    {
                        let mut b =
                            TextBundle::from_section("0", get_text_style_pet_amount(&font_assets))
                                .with_style(Style {
                                    position_type: PositionType::Absolute,
                                    right: Val::Percent(1.0),
                                    bottom: Val::Percent(0.0),
                                    display: Display::None,
                                    ..default()
                                });
                        b.z_index = ZIndex::Local(-1);
                        b
                    },
                    PetComponent::Amount,
                    PetIdComponent(pet.id.clone()),
                ));
            })
            .id();

        pet_ids.push(id);
    }

    // Player stats
    let stats = commands
        .spawn((
            NodeBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    display: Display::Flex,
                    flex_direction: FlexDirection::Row,
                    padding: UiRect::all(Val::Percent(2.0)),
                    ..default()
                },
                ..default()
            },
            Name::new("Player stats"),
        ))
        .with_children(|stats_root| {
            let node = NodeBundle {
                style: Style {
                    flex_grow: 1.0,
                    display: Display::Flex,
                    align_items: AlignItems::Center,
                    flex_direction: FlexDirection::Row,
                    ..default()
                },
                ..default()
            };

            let icon_style = Style {
                width: Val::Percent(25.0),
                margin: UiRect::right(Val::Percent(5.0)),
                ..default()
            };

            // Money
            stats_root
                .spawn((node.clone(), Name::new("Money")))
                .with_children(|money_root| {
                    // Money icon
                    money_root.spawn((
                        ImageBundle {
                            style: icon_style.clone(),
                            image: UiImage::new(gui_assets.money.clone()),
                            ..default()
                        },
                        Name::new("Money icon"),
                    ));

                    // Money text
                    money_root.spawn((
                        TextBundle::from_section(
                            format!("{:.0}", savegame.money),
                            get_text_style_default(&font_assets),
                        ),
                        PlayerStatsTextComponent::Money,
                        Name::new("Money text"),
                    ));
                });

            // Multiplier
            stats_root
                .spawn((node.clone(), Name::new("Multiplier")))
                .with_children(|multiplier_root| {
                    // Multiplier icon
                    multiplier_root.spawn((
                        ImageBundle {
                            style: icon_style,
                            image: UiImage::new(gui_assets.multiplier.clone()),
                            ..default()
                        },
                        Name::new("Multiplier icon"),
                    ));

                    // Multiplier text
                    multiplier_root.spawn((
                        TextBundle::from_section(
                            format!("{:.1}", savegame.multiplier),
                            get_text_style_default(&font_assets),
                        ),
                        PlayerStatsTextComponent::Multiplier,
                        Name::new("Multiplier text"),
                    ));
                });
        })
        .id();

    // Shop title
    let title = commands
        .spawn((
            ButtonBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    display: Display::Flex,
                    justify_content: JustifyContent::Center,
                    align_items: AlignItems::Center,
                    border: UiRect::vertical(Val::Percent(0.5)),
                    padding: UiRect::vertical(Val::Percent(1.0)),
                    ..default()
                },
                background_color: Srgba::new(80.0 / 255.0, 51.0 / 255.0, 51.0 / 255.0, 1.0).into(),
                border_color: Srgba::new(54.0 / 255.0, 42.0 / 255.0, 42.0 / 255.0, 1.0).into(),
                ..default()
            },
            ShopUIListToggleComponent,
            Name::new("Shop title"),
        ))
        .with_children(|title_root| {
            title_root.spawn((
                TextBundle::from_section(
                    localization.get(LineId::StoreTitle),
                    get_text_style_header(&font_assets),
                ),
                Name::new("Title"),
            ));
        })
        .id();

    // Shop control
    let control = commands
        .spawn((
            NodeBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    #[cfg(not(any(target_os = "android", target_os = "ios")))]
                    display: Display::Flex,
                    #[cfg(any(target_os = "android", target_os = "ios"))]
                    display: Display::None,
                    flex_direction: FlexDirection::Row,
                    padding: UiRect::all(Val::Percent(2.0)),
                    overflow: Overflow::clip(),
                    ..default()
                },
                background_color: STORE_CONTROL_BG_COLOR.into(),
                ..default()
            },
            ShopUIControlComponent,
            Name::new("Shop control"),
        ))
        .with_children(|control_root| {
            // Mode control
            control_root
                .spawn((
                    NodeBundle {
                        style: Style {
                            min_width: Val::Percent(50.0),
                            display: Display::Flex,
                            flex_direction: FlexDirection::Column,
                            margin: UiRect::right(Val::Percent(2.0)),
                            ..default()
                        },
                        ..default()
                    },
                    Name::new("Mode control"),
                ))
                .with_children(|mode_root| {
                    let button = ButtonBundle {
                        style: Style {
                            display: Display::Flex,
                            justify_content: JustifyContent::Center,
                            align_items: AlignItems::Center,
                            flex_grow: 1.0,
                            padding: UiRect::all(Val::Percent(2.0)),
                            margin: UiRect::bottom(Val::Percent(4.0)),
                            ..default()
                        },
                        background_color: color::PERU.into(),
                        ..default()
                    };

                    // Buy button
                    mode_root
                        .spawn((button.clone(), ShopMode::Buy, Name::new("Buy button")))
                        .with_children(|btn| {
                            btn.spawn(TextBundle::from_section(
                                localization.get(LineId::StoreModeBuy),
                                get_text_style_default(&font_assets),
                            ));
                        });

                    // Sell button
                    mode_root
                        .spawn((
                            {
                                let mut b = button.clone();
                                b.style.margin.bottom = Val::ZERO;
                                b
                            },
                            ShopMode::Sell,
                            Name::new("Sell button"),
                        ))
                        .with_children(|btn| {
                            btn.spawn(TextBundle::from_section(
                                localization.get(LineId::StoreModeSell),
                                get_text_style_default(&font_assets),
                            ));
                        });
                });

            // Multiplier control
            control_root
                .spawn((
                    NodeBundle {
                        style: Style {
                            display: Display::Flex,
                            flex_direction: FlexDirection::Row,
                            ..default()
                        },
                        ..default()
                    },
                    Name::new("Multiplier control"),
                ))
                .with_children(|mp_root| {
                    let button = ButtonBundle {
                        style: Style {
                            height: Val::Percent(100.0),
                            display: Display::Flex,
                            justify_content: JustifyContent::Center,
                            align_items: AlignItems::Center,
                            aspect_ratio: Some(1.0),
                            margin: UiRect::right(Val::Percent(4.0)),
                            ..default()
                        },
                        background_color: color::PERU.into(),
                        ..default()
                    };

                    // 1x button
                    mp_root
                        .spawn((button.clone(), ShopMultiplier::X1, Name::new("1x button")))
                        .with_children(|btn| {
                            btn.spawn(TextBundle::from_section(
                                localization.get(LineId::StoreMultiplier1x),
                                get_text_style_default(&font_assets),
                            ));
                        });

                    // 10x button
                    mp_root
                        .spawn((button.clone(), ShopMultiplier::X10, Name::new("10x button")))
                        .with_children(|btn| {
                            btn.spawn(TextBundle::from_section(
                                localization.get(LineId::StoreMultiplier10x),
                                get_text_style_default(&font_assets),
                            ));
                        });
                });
        })
        .id();

    // Shop list
    let list = commands
        .spawn((
            NodeBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    min_height: Val::Vh(0.0),
                    padding: UiRect::all(Val::Percent(2.0)),
                    #[cfg(not(any(target_os = "android", target_os = "ios")))]
                    display: Display::Flex,
                    #[cfg(any(target_os = "android", target_os = "ios"))]
                    display: Display::None,
                    flex_direction: FlexDirection::Row,
                    ..default()
                },
                background_color: STORE_LIST_BG_COLOR.into(),
                ..default()
            },
            ScrollView::default(),
            ShopUIListComponent,
            Name::new("Shop list"),
        ))
        .with_children(|list| {
            // Pet list
            list.spawn((
                NodeBundle {
                    style: Style {
                        width: Val::Percent(100.0),
                        display: Display::Flex,
                        flex_direction: FlexDirection::Column,
                        flex_grow: 1.0,
                        ..default()
                    },
                    ..default()
                },
                Name::new("Content"),
                ScrollableContent::default(),
            ))
            .push_children(pet_ids.as_slice());
        })
        .id();

    // Root
    let mut root = commands.spawn((
        NodeBundle {
            style: Style {
                #[cfg(not(any(target_os = "android", target_os = "ios")))]
                width: Val::Percent(25.0),
                #[cfg(any(target_os = "android", target_os = "ios"))]
                width: Val::Percent(100.0),
                #[cfg(not(any(target_os = "android", target_os = "ios")))]
                height: Val::Percent(100.0),
                #[cfg(not(any(target_os = "android", target_os = "ios")))]
                border: UiRect::right(Val::Percent(0.2)),
                display: Display::Flex,
                flex_direction: FlexDirection::Column,
                ..default()
            },
            border_color: Srgba::new(54.0 / 255.0, 42.0 / 255.0, 42.0 / 255.0, 1.0).into(),
            background_color: STORE_BG_COLOR.into(),
            ..default()
        },
        GameObjectComponent,
        Name::new("Shop UI"),
    ));

    #[cfg(not(any(target_os = "android", target_os = "ios")))]
    root.push_children(&[title, control, list, stats]);
    #[cfg(any(target_os = "android", target_os = "ios"))]
    root.push_children(&[stats, title, control, list]);
}

pub fn listen_shop_control_changes(
    mut mode_button_query: Query<
        (&ShopMode, &mut BackgroundColor, &Interaction),
        (With<ShopMode>, Without<ShopMultiplier>),
    >,
    mut mp_button_query: Query<
        (&ShopMultiplier, &mut BackgroundColor, &Interaction),
        (Without<ShopMode>, With<ShopMultiplier>),
    >,
    mut settings: ResMut<ShopSettings>,
) {
    for (mode, mut bg, i) in mode_button_query.iter_mut() {
        let color = match (*mode, *mode == settings.mode) {
            (ShopMode::Buy, false) => color::LIMEGREEN,
            (ShopMode::Buy, true) => color::DARK_GREEN,
            (ShopMode::Sell, false) => color::CRIMSON,
            (ShopMode::Sell, true) => color::DARK_RED,
        };

        match *i {
            Interaction::None => *bg = color.into(),
            Interaction::Hovered => *bg = color.lighter(0.1).into(),
            Interaction::Pressed => {
                *bg = color.darker(0.1).into();
                settings.mode = *mode;
            }
        }
    }

    for (multiplier, mut bg, i) in mp_button_query.iter_mut() {
        let color = if *multiplier == settings.multiplier {
            color::PERU.darker(0.2)
        } else {
            color::PERU
        };

        match *i {
            Interaction::None => *bg = color.into(),
            Interaction::Hovered => *bg = color.lighter(0.1).into(),
            Interaction::Pressed => {
                *bg = color.darker(0.1).into();
                settings.multiplier = *multiplier;
            }
        }
    }
}

pub fn update_player_stats(
    mut money_query: Query<(&mut Text, &PlayerStatsTextComponent), With<PlayerStatsTextComponent>>,
    savegame: Res<Persistent<Savegame>>,
) {
    if !savegame.is_changed() {
        return;
    }

    for (mut t, c) in money_query.iter_mut() {
        // ome
        if let Some(&mut ref mut section) = t.sections.first_mut() {
            section.value = if *c == PlayerStatsTextComponent::Money {
                format!("{:.0}", savegame.money)
            } else {
                format!("{:.1}", savegame.multiplier)
            }
        }
    }
}

pub fn toggle_pet_nodes(
    mut commands: Commands,
    savegame: Res<Persistent<Savegame>>,
    mut query: Query<
        (
            Entity,
            &mut BackgroundColor,
            Option<&mut Text>,
            Option<&PetDisabledComponent>,
            &PetIdComponent,
            &PetComponent,
        ),
        (With<PetIdComponent>, With<PetComponent>),
    >,
    data_assets: Res<DataAssets>,
    pets_assets: Res<Assets<Pets>>,
    shop_settings: Res<ShopSettings>,
) {
    let pets = pets_assets.get(data_assets.pets.id()).unwrap();

    for (e, mut bg, mut text, d, id, part) in query.iter_mut() {
        let pet = pets.0.iter().find(|x| x.id.eq(&id.0));

        if pet.is_none() {
            *bg = color::PURPLE.into();
            if *part == PetComponent::Base {
                commands.entity(e).insert(PetDisabledComponent);
            }
            continue;
        }

        let pet = pet.unwrap();

        let multiplier = shop_settings.multiplier.as_i32();
        let amount = *savegame.pets.get(&pet.id).unwrap_or(&0);
        let mut price = pet.price * 1.15_f64.powi(amount as i32 + multiplier);

        if shop_settings.mode == ShopMode::Sell {
            price /= 4.0;
        }

        price = price.trunc();

        if ((shop_settings.mode == ShopMode::Buy && price > savegame.money.trunc())
            || (shop_settings.mode == ShopMode::Sell && amount as i32 - multiplier < 0))
            && d.is_none()
        {
            match (part, text) {
                (PetComponent::Base, _) => {
                    *bg = STORE_ITEM_DISABLED_BG_COLOR.into();
                }
                (PetComponent::Name, Some(mut text)) => {
                    text.sections[0].style.color = color::GRAY.into();
                }
                (PetComponent::Price, Some(mut text)) => {
                    text.sections[0].style.color = color::RED.into();
                }
                _ => {}
            }

            commands.entity(e).insert(PetDisabledComponent);

            continue;
        }

        if ((shop_settings.mode == ShopMode::Buy && price <= savegame.money.trunc())
            || (shop_settings.mode == ShopMode::Sell && amount as i32 - multiplier >= 0))
            && d.is_some()
        {
            match (part, text) {
                (PetComponent::Base, _) => {
                    *bg = STORE_ITEM_BG_COLOR.into();
                }
                (PetComponent::Name, Some(mut text)) => {
                    text.sections[0].style.color = color::WHITE.into();
                }
                (PetComponent::Price, Some(mut text)) => {
                    text.sections[0].style.color = color::LIME.into();
                }
                _ => {}
            }

            commands.entity(e).remove::<PetDisabledComponent>();

            continue;
        }
    }
}

pub fn pet_node_interaction(
    mut commands: Commands,
    query: Query<
        (&Interaction, &PetIdComponent),
        (
            With<PetComponent>,
            With<PetIdComponent>,
            Without<PetDisabledComponent>,
            Changed<Interaction>,
        ),
    >,
    mut savegame: ResMut<Persistent<Savegame>>,
    shop_settings: Res<ShopSettings>,
    data_assets: Res<DataAssets>,
    pets_assets: Res<Assets<Pets>>,
    sfx_assets: Res<SFXAssets>,
    mut purchase_event_writer: EventWriter<PurchaseEvent>,
) {
    let pets = pets_assets.get(data_assets.pets.id()).unwrap();

    for (i, id) in query.iter() {
        if *i != Interaction::Pressed {
            continue;
        }

        let id = &id.0;
        let pet = pets.0.iter().find(|x| x.id.eq(id)).unwrap();

        let multiplier = shop_settings.multiplier.as_i32();
        let amount = *savegame.pets.get(id).unwrap_or(&0);
        let mut price = pet.price * 1.15_f64.powi(amount as i32 + multiplier);

        if shop_settings.mode == ShopMode::Sell {
            price /= 4.0;
        }

        price = price.trunc();

        let source: &Handle<AudioSource>;

        if shop_settings.mode == ShopMode::Buy {
            savegame.money -= price;
            savegame.multiplier += pet.multiplier * multiplier as f64;
            savegame
                .pets
                .entry(id.clone())
                .and_modify(|x| *x += multiplier as u32)
                .or_insert(multiplier as u32);
            source = &sfx_assets.purchase;
        } else {
            savegame.money += price;
            savegame.multiplier -= pet.multiplier * multiplier as f64;
            savegame
                .pets
                .entry(id.clone())
                .and_modify(|x| *x -= multiplier as u32);
            source = &sfx_assets.sell;
        }

        purchase_event_writer.send(PurchaseEvent);
        commands.spawn(AudioBundle {
            source: source.clone(),
            settings: PlaybackSettings {
                mode: bevy::audio::PlaybackMode::Despawn,
                ..default()
            },
        });
    }
}

pub fn update_pet_nodes(
    mut query: Query<
        (&mut Text, &PetIdComponent, &PetComponent),
        (With<PetComponent>, With<PetIdComponent>, With<Text>),
    >,
    savegame: Res<Persistent<Savegame>>,
    shop_settings: Res<ShopSettings>,
    data_assets: Res<DataAssets>,
    pets_assets: Res<Assets<Pets>>,
) {
    let pets = pets_assets.get(data_assets.pets.id()).unwrap();

    for (mut text, id, comp) in query.iter_mut() {
        if comp != &PetComponent::Price {
            continue;
        }

        let id = &id.0;

        let pet = pets.0.iter().find(|x| x.id.eq(id)).unwrap();

        let multiplier = shop_settings.multiplier.as_i32();
        let amount = *savegame.pets.get(id).unwrap_or(&0);
        let mut price = pet.price * 1.15_f64.powi(amount as i32 + multiplier);

        if shop_settings.mode == ShopMode::Sell {
            price /= 4.0;
        }

        text.sections[0].value = price.trunc().to_string();
    }
}

pub fn update_pet_amount(
    savegame: Res<Persistent<Savegame>>,
    mut text_query: Query<
        (&mut Text, &mut Style, &PetIdComponent, &PetComponent),
        (With<PetComponent>, With<PetIdComponent>, With<Text>),
    >,
) {
    for (mut text, mut style, id, comp) in text_query.iter_mut() {
        if comp != &PetComponent::Amount {
            continue;
        }

        let id = &id.0;

        if let Some(amount) = savegame.pets.get(id) {
            if *amount == 0 {
                style.display = Display::None;
                continue;
            }

            style.display = Display::Block;
            text.sections[0].value = amount.to_string();
        }
    }
}

#[cfg(any(target_os = "android", target_os = "ios"))]
pub fn toggle_shop_list(
    button_query: Query<&Interaction, (With<ShopUIListToggleComponent>, Changed<Interaction>)>,
    mut query: Query<&mut Style, Or<(With<ShopUIControlComponent>, With<ShopUIListComponent>)>>,
) {
    for i in button_query.iter() {
        if *i == Interaction::Pressed {
            println!("press");
            for mut style in query.iter_mut() {
                style.display = if style.display == Display::None {
                    Display::Flex
                } else {
                    Display::None
                };
                println!("changed");
            }
        }
    }
}
