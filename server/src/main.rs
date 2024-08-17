use std::net::SocketAddr;

use bevy::{
    log::LogPlugin,
    prelude::*,
    tasks::{TaskPool, TaskPoolBuilder},
};
use bevy_eventwork::{
    tcp::{NetworkSettings, TcpProvider},
    EventworkRuntime, Network,
};

fn main() {
    let mut app = App::new();

    app.add_plugins((MinimalPlugins, LogPlugin::default()));

    app.add_plugins(bevy_eventwork::EventworkPlugin::<
        TcpProvider,
        bevy::tasks::TaskPool,
    >::default());

    app.insert_resource(EventworkRuntime(
        TaskPoolBuilder::new().num_threads(2).build(),
    ));

    app.insert_resource(NetworkSettings::default());

    app.add_systems(Startup, setup_networking);

    app.run();
}

fn setup_networking(
    mut net: ResMut<Network<TcpProvider>>,
    settings: Res<NetworkSettings>,
    task_pool: Res<EventworkRuntime<TaskPool>>,
) {
    let ip_address = "0.0.0.0".parse().expect("Could not parse ip address");

    info!("Address of the server: {}", ip_address);

    if let Err(err) = net.listen(SocketAddr::new(ip_address, 10909), &task_pool.0, &settings) {
        error!("Could not start listening: {}", err);
        panic!();
    }

    info!("Started listening for new connections!");
}
