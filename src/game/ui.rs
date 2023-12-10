use crate::{
    assets::AppAssets,
    localization::{LineId, Localization},
    style::{
        get_category_header_text_style, get_item_desc_text_style, get_item_header_text_style,
        CONTROLPANEL_BG_COLOR, ITEM_BG_ACTIVE_COLOR, ITEM_BORDER_COLOR,
    },
};
use bevy::prelude::*;
use bevy_persistent::Persistent;

use super::{
    item::{ItemComponent, Items},
    player::PlayerData,
};

#[derive(Component)]
pub struct UiTextMoneyComponent;

#[derive(Component)]
pub struct UiTextItemCostComponent(pub String);

#[derive(Component)]
pub struct UiTextItemHeaderComponent(pub String);

#[derive(Component)]
pub struct UiTextItemIconComponent(pub String);

#[derive(Component)]
pub struct UiInventory;

#[derive(Component)]
pub enum UiButtonControl {
    Happiness,
    Hunger,
    Fatigue,
}

pub fn generate_ui(mut commands: Commands, app_assets: Res<AppAssets>) {
    commands
        .spawn(NodeBundle {
            style: Style {
                flex_direction: FlexDirection::Column,
                width: Val::Percent(100.0),
                height: Val::Percent(100.0),
                ..default()
            },
            background_color: Color::NONE.into(),
            ..default()
        })
        .with_children(|parent| {
            // Empty space
            parent.spawn(NodeBundle {
                style: Style {
                    flex_grow: 6.0,
                    ..default()
                },
                background_color: Color::NONE.into(),
                ..default()
            });

            // Control panel
            parent
                .spawn(NodeBundle {
                    style: Style {
                        justify_content: JustifyContent::SpaceBetween,
                        align_items: AlignItems::Center,
                        flex_grow: 0.0,
                        ..default()
                    },
                    background_color: CONTROLPANEL_BG_COLOR.into(),
                    ..default()
                })
                // Hunger
                .with_children(|panel| {
                    panel
                        .spawn((
                            ButtonBundle {
                                style: Style {
                                    margin: UiRect::all(Val::Px(12.0)),
                                    max_width: Val::Px(64.0),
                                    aspect_ratio: Some(1.0),
                                    ..default()
                                },
                                background_color: Color::YELLOW_GREEN.into(),
                                ..default()
                            },
                            UiButtonControl::Happiness,
                        ))
                        .with_children(|button| {
                            button.spawn(ImageBundle {
                                image: UiImage::new(app_assets.ui_btn_happiness.clone()),
                                ..default()
                            });
                        });
                    panel
                        .spawn((
                            ButtonBundle {
                                style: Style {
                                    margin: UiRect::all(Val::Px(12.0)),
                                    max_width: Val::Px(64.0),
                                    aspect_ratio: Some(1.0),
                                    ..default()
                                },
                                background_color: Color::YELLOW_GREEN.into(),
                                ..default()
                            },
                            UiButtonControl::Hunger,
                        ))
                        .with_children(|button| {
                            button.spawn(ImageBundle {
                                image: UiImage::new(app_assets.ui_btn_hunger.clone()),
                                ..default()
                            });
                        });
                    panel
                        .spawn((
                            ButtonBundle {
                                style: Style {
                                    margin: UiRect::all(Val::Px(12.0)),
                                    max_width: Val::Px(64.0),
                                    aspect_ratio: Some(1.0),
                                    ..default()
                                },
                                background_color: Color::YELLOW_GREEN.into(),
                                ..default()
                            },
                            UiButtonControl::Fatigue,
                        ))
                        .with_children(|button| {
                            button.spawn(ImageBundle {
                                image: UiImage::new(app_assets.ui_btn_fatigue.clone()),
                                ..default()
                            });
                        });
                });
        });
}

pub fn update_ui(
    player_data: Res<Persistent<PlayerData>>,
    mut money_text_query: Query<&mut Text, With<UiTextMoneyComponent>>,
) {
    if let Ok(mut money_text) = money_text_query.get_single_mut() {
        if let Some(mut text) = money_text.sections.get_mut(0) {
            text.value = player_data.money.to_string();
        }
    }
}
