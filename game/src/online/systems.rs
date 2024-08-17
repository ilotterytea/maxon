use bevy::prelude::*;
use bevy_mod_reqwest::{BevyReqwest, ReqwestResponseEvent, StatusCode};
use bevy_persistent::Persistent;
use serde_json::json;

use crate::{
    animation::ThugshakerAnimation,
    constants::SIGNIN_API_URL,
    online::ui::{LoginButton, LoginUsernameText},
};

use super::{signin::SigninCredentials, UserData};

#[derive(Resource)]
pub(super) struct UserDataStatus;

pub(super) fn setup_user_data(
    mut commands: Commands,
    mut client: BevyReqwest,
    user_data: Option<Res<UserData>>,
    status: Option<Res<UserDataStatus>>,
    credentials: Res<Persistent<SigninCredentials>>,
) {
    if status.is_some() || user_data.is_some() {
        return;
    }

    let Some(ct) = &credentials.client_token else {
        return;
    };

    let Some(at) = &credentials.access_token else {
        return;
    };

    commands.insert_resource(UserDataStatus);

    let body = json!({
        "client_token": ct,
        "access_token": at
    });

    let request = client
        .post(format!("{}/identify", SIGNIN_API_URL))
        .json(&body)
        .build()
        .unwrap();

    client
        .send(request)
        .on_response(user_data_on_client_response);
}

fn user_data_on_client_response(
    trigger: Trigger<ReqwestResponseEvent>,
    mut commands: Commands,
    mut text_query: Query<&mut Text, With<LoginUsernameText>>,
    mut creds: ResMut<Persistent<SigninCredentials>>,
    btn_query: Query<Entity, With<LoginButton>>,
) {
    let response = trigger.event();

    if response.status() != StatusCode::OK {
        warn!("Failed to identify token. Resetting the data...");
        commands.remove_resource::<UserDataStatus>();

        creds.access_token = None;
        creds.client_token = None;
        creds.persist().expect("Failed to save credentials");

        return;
    }

    let body: serde_json::Value = response.deserialize_json().unwrap();
    let username = body["data"]["username"].as_str().unwrap();
    commands.insert_resource(UserData {
        username: username.into(),
    });
    text_query.iter_mut().for_each(|mut text| {
        text.sections[0].value = format!("Signed in as\n{}", username);
    });
    btn_query.iter().for_each(|e| {
        commands.entity(e).remove::<ThugshakerAnimation>();
    });
    commands.remove_resource::<UserDataStatus>();
}
