use bevy::{color::palettes::css as color, prelude::*};
use bevy_mod_reqwest::{BevyReqwest, ReqwestResponseEvent};
use bevy_persistent::Persistent;
use serde_json::json;

use crate::{
    animation::ThugshakerAnimation, constants::SIGNIN_API_URL, style::get_text_style_default,
    FontAssets, GUIAssets,
};

use super::{
    signin::{CreateServerEvent, SigninCredentials},
    UserData,
};

#[derive(Component)]
pub struct LoginButton(pub bool);

#[derive(Component)]
pub struct LoginUsernameText;

#[derive(Resource)]
pub(super) struct LoginStatus;

pub fn get_login_button() -> (ButtonBundle, LoginButton, ThugshakerAnimation, Name) {
    (
        ButtonBundle {
            style: Style {
                width: Val::Px(192.0),
                height: Val::Px(64.0),
                border: UiRect::all(Val::Px(1.0)),
                ..default()
            },
            border_color: color::BLACK.into(),
            background_color: color::INDIGO.into(),
            ..default()
        },
        LoginButton(false),
        ThugshakerAnimation,
        Name::new("Login button"),
    )
}

pub(super) fn setup_login_button(
    mut commands: Commands,
    credentials: Res<Persistent<SigninCredentials>>,
    user_data: Option<Res<UserData>>,
    mut query: Query<
        (
            Entity,
            &mut LoginButton,
            &mut BackgroundColor,
            &mut BorderColor,
            &mut Style,
            Option<&Children>,
        ),
        With<LoginButton>,
    >,
    font_assets: Res<FontAssets>,
    gui_assets: Res<GUIAssets>,
) {
    let Ok((e, mut btn, mut bg, mut brg, mut s, c)) = query.get_single_mut() else {
        return;
    };

    if c.is_some() && !credentials.is_changed() {
        return;
    }

    commands.entity(e).despawn_descendants();

    btn.0 = credentials.access_token.is_some() && credentials.client_token.is_some();

    let base = commands
        .spawn((
            NodeBundle {
                style: Style {
                    width: Val::Percent(100.0),
                    height: Val::Percent(100.0),
                    flex_direction: FlexDirection::Row,
                    justify_content: if btn.0 {
                        JustifyContent::FlexEnd
                    } else {
                        JustifyContent::SpaceAround
                    },
                    align_items: AlignItems::Center,
                    ..default()
                },
                ..default()
            },
            Name::new("Base"),
        ))
        .id();

    if btn.0 {
        *bg = Srgba::rgba_u8(0, 0, 0, 0).into();
        *brg = Srgba::rgba_u8(0, 0, 0, 0).into();
        s.border = UiRect::ZERO;
        s.width = Val::Px(192.0);

        let node = commands
            .spawn((
                TextBundle::from_section(
                    if let Some(user_data) = user_data {
                        format!("Signed in as\n{}", user_data.username)
                    } else {
                        "Logging in...".into()
                    },
                    get_text_style_default(&font_assets),
                )
                .with_text_justify(JustifyText::Right)
                .with_no_wrap(),
                LoginUsernameText,
                Name::new("Text"),
            ))
            .id();

        commands.entity(base).add_child(node);
    } else {
        *bg = color::REBECCA_PURPLE.into();
        *brg = color::BLACK.into();
        s.border = UiRect::all(Val::Px(1.0));
        s.width = Val::Px(236.0);

        let icon = commands
            .spawn((
                ImageBundle {
                    image: UiImage::new(gui_assets.link_twitch.clone()),
                    style: Style {
                        width: Val::Px(32.0),
                        height: Val::Px(32.0),
                        ..default()
                    },
                    ..default()
                },
                Name::new("Icon"),
            ))
            .id();

        let text = commands
            .spawn((
                TextBundle::from_section(
                    "Log in with Twitch",
                    get_text_style_default(&font_assets),
                ),
                Name::new("Text"),
            ))
            .id();

        commands.entity(base).push_children(&[icon, text]);
    }

    commands.entity(e).add_child(base);
}

pub(super) fn login(
    mut commands: Commands,
    mut client: BevyReqwest,
    login_status: Option<Res<LoginStatus>>,
    query: Query<(&Interaction, &LoginButton), (With<LoginButton>, Changed<Interaction>)>,
) {
    if login_status.is_some() {
        return;
    }

    for (i, btn) in query.iter() {
        if btn.0 || *i != Interaction::Pressed {
            continue;
        }

        commands.insert_resource(LoginStatus);

        let body = json!({
            "agent": {
                "name": "Maxon",
                "version": 1
            },
            "auth_service": "ttv",
            "request_user": false
        });

        let request = client
            .get(format!("{}/authenticate", SIGNIN_API_URL))
            .json(&body)
            .build()
            .unwrap();

        client.send(request).on_response(
            |trigger: Trigger<ReqwestResponseEvent>,
             mut commands: Commands,
             mut writer: EventWriter<CreateServerEvent>| {
                let response = trigger.event();
                let body: serde_json::Value = response.deserialize_json().unwrap();
                let url = body["data"]["url"].as_str().unwrap();
                writer.send(CreateServerEvent);
                commands.remove_resource::<LoginStatus>();
                open::that(url).expect("Failed to open auth URL");
            },
        );
    }
}
