use bevy::prelude::*;
use bevy_mod_picking::prelude::*;

use crate::{game::components::GameObjectComponent, AppState};

#[derive(Component)]
pub struct MinigamesTriggerComponent;

pub fn spawn_minigames_trigger(
    mut commands: Commands,
    mut meshes: ResMut<Assets<Mesh>>,
    mut materials: ResMut<Assets<StandardMaterial>>,
) {
    commands.spawn((
        PbrBundle {
            mesh: meshes.add(Cuboid::new(5.0, 2.5, 0.01)),
            material: materials.add(Color::srgba(0.0, 0.0, 0.0, 0.0)),
            transform: Transform::from_xyz(3.0, 3.7, 5.0),
            ..default()
        },
        On::<Pointer<Click>>::run(click_on_minigames_trigger),
        Name::new("Minigames trigger"),
        MinigamesTriggerComponent,
        GameObjectComponent,
    ));
}

pub fn click_on_minigames_trigger(mut next_state: ResMut<NextState<AppState>>) {
    next_state.set(AppState::MinigamesLobby);
}
