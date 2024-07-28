use bevy::prelude::*;

#[derive(Component)]
pub struct MenuObjectComponent;

pub fn despawn_menu_objects(
    mut commands: Commands,
    query: Query<Entity, With<MenuObjectComponent>>,
) {
    for e in query.iter() {
        commands.entity(e).despawn_recursive();
    }
}
