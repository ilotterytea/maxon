#[cfg(feature = "debug")]
use bevy::prelude::*;

#[cfg(feature = "debug")]
use bevy_persistent::Persistent;

#[cfg(feature = "debug")]
use crate::{
    constants::APP_STYLIZED_NAME,
    persistent::Settings,
    style::{get_text_style_debug, get_text_style_debug_value},
    systems::CameraComponent,
    AppState, FontAssets,
};

#[cfg(feature = "debug")]
pub struct DebugPlugin;

#[cfg(feature = "debug")]
impl Plugin for DebugPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(OnEnter(AppState::Boot), setup_debug_info)
            .add_systems(Update, (update_debug_info, listen_keyboard))
            .insert_resource(DebugMetrics::default());
    }
}

#[cfg(feature = "debug")]
#[derive(Resource, Default)]
pub struct DebugMetrics {
    pub frame_count: u64,
    pub elapsed_time: f64,
}

#[cfg(feature = "debug")]
#[derive(Component)]
pub struct DebugBaseComponent;

#[cfg(feature = "debug")]
#[derive(Component, PartialEq, Eq)]
pub enum DebugComponent {
    GameInfo,
    Fps,
    FrameTime,
    Entities,
}

#[cfg(feature = "debug")]
fn setup_debug_info(
    mut commands: Commands,
    font_assets: Res<FontAssets>,
    query: Query<&DebugComponent>,
    settings: Res<Persistent<Settings>>,
    camera_query: Query<Entity, With<CameraComponent>>,
) {
    if !query.is_empty() {
        return;
    }

    let camera_entity = camera_query.single();

    if settings.debug.fly {
        commands.entity(camera_entity).insert(bevy_flycam::FlyCam);
    }

    commands
        .spawn((
            NodeBundle {
                style: Style {
                    position_type: PositionType::Absolute,
                    top: Val::Percent(0.2),
                    right: Val::Percent(0.2),
                    padding: UiRect::all(Val::Percent(0.5)),
                    align_items: AlignItems::End,
                    flex_direction: FlexDirection::Column,
                    display: if settings.debug.info {
                        Display::Flex
                    } else {
                        Display::None
                    },
                    ..default()
                },
                background_color: Srgba::new(0.0, 0.0, 0.0, 90.0 / 255.0).into(),
                ..default()
            },
            Name::new("Debug info"),
            DebugBaseComponent,
        ))
        .with_children(|root| {
            // Game info
            root.spawn((
                TextBundle::from_section(
                    format!("{} {}", APP_STYLIZED_NAME, env!("CARGO_PKG_VERSION")),
                    get_text_style_debug(&font_assets),
                ),
                DebugComponent::GameInfo,
                Name::new("Debug game info"),
            ));

            // FPS
            root.spawn((
                TextBundle::from_sections([
                    TextSection::new("0", get_text_style_debug_value(&font_assets)),
                    TextSection::new(" fps", get_text_style_debug(&font_assets)),
                ]),
                DebugComponent::Fps,
                Name::new("Debug fps info"),
            ));

            // Frame time
            root.spawn((
                TextBundle::from_sections([
                    TextSection::new("0", get_text_style_debug_value(&font_assets)),
                    TextSection::new(" between frames", get_text_style_debug(&font_assets)),
                ]),
                DebugComponent::FrameTime,
                Name::new("Debug frame time info"),
            ));

            // Entities
            root.spawn((
                TextBundle::from_sections([
                    TextSection::new("0", get_text_style_debug_value(&font_assets)),
                    TextSection::new(" entities", get_text_style_debug(&font_assets)),
                ]),
                DebugComponent::Entities,
                Name::new("Debug entities count info"),
            ));
        });
}

#[cfg(feature = "debug")]
fn update_debug_info(
    time: Res<Time>,
    mut metrics: ResMut<DebugMetrics>,
    mut query: Query<(&mut Text, &DebugComponent), With<Text>>,
    entities_query: Query<Entity>,
) {
    metrics.frame_count += 1;
    metrics.elapsed_time += time.delta_seconds_f64();

    if metrics.elapsed_time >= 1.0 {
        let fps = metrics.frame_count as f64 / metrics.elapsed_time;
        let frame_time = metrics.elapsed_time / metrics.frame_count as f64 * 1000.0;

        for (mut text, comp) in query.iter_mut() {
            if comp == &DebugComponent::GameInfo {
                continue;
            }

            let mut section = &mut text.sections[0];

            section.value = match comp {
                DebugComponent::Fps => format!("{:.2}", fps),
                DebugComponent::FrameTime => format!("{:.2} ms", frame_time),
                DebugComponent::Entities => entities_query.iter().len().to_string(),
                _ => "N/A".into(),
            }
        }

        // Reset metrics for the next second
        metrics.frame_count = 0;
        metrics.elapsed_time = 0.0;
    }
}

#[cfg(feature = "debug")]
fn listen_keyboard(
    mut commands: Commands,
    keyboard_input: Res<ButtonInput<KeyCode>>,
    mut settings: ResMut<Persistent<Settings>>,
    mut debug_info_query: Query<&mut Style, With<DebugBaseComponent>>,
    camera_query: Query<Entity, With<CameraComponent>>,
) {
    // Debug info
    if keyboard_input.just_pressed(KeyCode::F3) {
        if let Ok(mut style) = debug_info_query.get_single_mut() {
            settings.debug.info = !settings.debug.info;

            if settings.debug.info {
                style.display = Display::Flex;
            } else {
                style.display = Display::None;
            }

            settings.persist().expect("Failed to save the settings");
        }
    }

    // Flycam
    if keyboard_input.just_pressed(KeyCode::F4) {
        if let Ok(entity) = camera_query.get_single() {
            settings.debug.fly = !settings.debug.fly;

            if settings.debug.fly {
                commands.entity(entity).insert(bevy_flycam::FlyCam);
            } else {
                commands.entity(entity).remove::<bevy_flycam::FlyCam>();
            }

            settings.persist().expect("Failed to save the settings");
        }
    }
}
