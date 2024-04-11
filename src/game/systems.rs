use std::f32::consts::PI;

use bevy::prelude::*;

use crate::{assets::AppAssets, constants::ROOM_LIGHTS};

use super::{bedroom::systems::GameBedroomLightComponent, RoomState, UiButtonControl};

#[derive(Component)]
pub struct GameLivingroomLightComponent;

pub fn generate_game_scene(mut commands: Commands, app_assets: Res<AppAssets>) {
    // Living room
    commands.spawn((
        SceneBundle {
            scene: app_assets.mdl_maxon_room.clone(),
            ..default()
        },
        Name::new("Living Room"),
    ));

    commands.spawn((
        PointLightBundle {
            point_light: PointLight {
                intensity: 5000.0,
                range: 15.0,
                shadows_enabled: true,
                color: ROOM_LIGHTS[0],
                ..default()
            },
            transform: Transform::from_xyz(0.0, 7.2, 0.0),
            ..default()
        },
        GameLivingroomLightComponent,
        Name::new("Living Room Light"),
    ));
}

pub fn update_camera_transform(
    state: Res<State<RoomState>>,
    mut camera_query: Query<&mut Transform, With<Camera>>,
) {
    if !state.is_changed() {
        return;
    }

    for mut t in camera_query.iter_mut() {
        let trs = state.get_camera_transform();
        let pos = trs.0;
        let rot = trs.1;

        t.translation = Vec3::from_array(pos);
        t.rotation = Quat::from_rotation_y(rot * PI / 180.0 * 2.2);
    }
}

pub fn handle_control_buttons(
    mut state: ResMut<NextState<RoomState>>,
    query: Query<(&Interaction, &UiButtonControl), (With<UiButtonControl>, Changed<Interaction>)>,
) {
    for (i, b) in query.iter() {
        if *i != Interaction::Pressed {
            continue;
        }

        let s = match b {
            UiButtonControl::Hunger => RoomState::Kitchen,
            UiButtonControl::Happiness => RoomState::LivingRoom,
            UiButtonControl::Fatigue => RoomState::Bedroom,
        };

        state.set(s);
    }
}

pub fn update_light_intensity(
    state: Res<State<RoomState>>,
    mut living_room_lights: Query<
        &mut PointLight,
        (
            With<GameLivingroomLightComponent>,
            Without<GameBedroomLightComponent>,
        ),
    >,
    mut bedroom_lights: Query<
        (&mut PointLight, &GameBedroomLightComponent),
        (
            With<GameBedroomLightComponent>,
            Without<GameLivingroomLightComponent>,
        ),
    >,
) {
    if !state.is_changed() {
        return;
    }

    let (lr_intensity, (bd_intensity, bd_intensity_2)) = match **state {
        RoomState::Bedroom => (0.0, (1000.0, 200.0)),
        _ => (5000.0, (0.0, 0.0)),
    };

    for mut p in living_room_lights.iter_mut() {
        p.intensity = lr_intensity;
    }

    for (mut p, l) in bedroom_lights.iter_mut() {
        p.intensity = match *l {
            GameBedroomLightComponent::Primary => bd_intensity,
            GameBedroomLightComponent::Secondary => bd_intensity_2,
        };
    }
}
