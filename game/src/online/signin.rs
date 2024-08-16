use std::sync::Arc;

use axum::{
    extract::{Query as AxumQuery, State},
    response::Html,
    routing::get,
    Router,
};
use bevy::prelude::*;
use bevy_persistent::prelude::*;
use bevy_tokio_tasks::TokioTasksRuntime;
use serde::{Deserialize, Serialize};
use tokio::sync::Mutex;

use crate::AppState;

pub struct SigninIntegrationPlugin;

impl Plugin for SigninIntegrationPlugin {
    fn build(&self, app: &mut App) {
        app.add_event::<CreateServerEvent>()
            .add_systems(Startup, startup)
            .add_systems(
                Update,
                create_server
                    .run_if(in_state(AppState::Menu).and_then(on_event::<CreateServerEvent>())),
            );
    }
}

#[derive(Event)]
pub struct CreateServerEvent;

#[derive(Resource, Deserialize, Serialize, Default)]
pub struct SigninCredentials {
    pub client_token: Option<String>,
    pub access_token: Option<String>,
}

fn startup(mut commands: Commands) {
    let dir = dirs::config_dir()
        .expect("Failed to get a path to the configuration directory")
        .join(crate::constants::APP_DEVELOPER)
        .join(crate::constants::APP_NAME);

    commands.insert_resource(
        Persistent::<SigninCredentials>::builder()
            .name("Sign-in(tm) credentials")
            .format(StorageFormat::Bincode)
            .path(dir.join("credentials.maxon"))
            .default(SigninCredentials::default())
            .revertible(true)
            .build()
            .expect("Failed to initialize savegame"),
    );
}

#[derive(Resource)]
struct ServerStatus;

#[derive(Default)]
struct SigninState {
    pub client_token: Option<String>,
    pub access_token: Option<String>,
}

fn create_server(
    mut commands: Commands,
    x: Option<Res<ServerStatus>>,
    runtime: ResMut<TokioTasksRuntime>,
) {
    if x.is_none() {
        commands.insert_resource(ServerStatus);
    }

    runtime.spawn_background_task(|mut ctx| async move {
        let state = Arc::new(Mutex::new(SigninState::default()));
        let (tx, rx) = tokio::sync::oneshot::channel::<()>();

        let checker = tokio::task::spawn({
            let state = state.clone();
            async move {
                loop {
                    let s = state.lock().await;

                    if s.client_token.is_some() && s.access_token.is_some() {
                        tx.send(()).unwrap();
                        ctx.run_on_main_thread({
                            let c = s.client_token.clone();
                            let a = s.access_token.clone();
                            move |ctx| {
                                if let Some(mut res) = ctx
                                    .world
                                    .get_resource_mut::<Persistent<SigninCredentials>>()
                                {
                                    res.client_token.clone_from(&c);
                                    res.access_token.clone_from(&a);
                                    res.persist().expect("Failed to save credentials");

                                    ctx.world.remove_resource::<ServerStatus>();
                                }
                            }
                        })
                        .await;
                        break;
                    }
                }
            }
        });

        let router = Router::new()
            .route("/accept", get(accept_credentials))
            .with_state(state);

        let listener = tokio::net::TcpListener::bind("127.0.0.1:12891")
            .await
            .unwrap();

        let server = axum::serve(listener, router).with_graceful_shutdown(async {
            rx.await.ok();
        });

        tokio::select! {
            _ = server => {
                debug!("Login server has been stopped")
            }
            _ = checker => {
                debug!("Info updater has been stopped")
            }
        }
    });
}

#[derive(Deserialize)]
struct Params {
    pub client_token: String,
    pub access_token: String,
}

async fn accept_credentials(
    AxumQuery(query): AxumQuery<Params>,
    State(state): State<Arc<Mutex<SigninState>>>,
) -> Html<&'static str> {
    let mut s = state.lock().await;
    s.access_token = Some(query.access_token);
    s.client_token = Some(query.client_token);

    Html("Now you can close this tab")
}
