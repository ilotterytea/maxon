use bevy::prelude::*;

#[derive(Component)]
pub struct CameraComponent;

pub fn setup_camera(mut commands: Commands) {
    let mut e = commands.spawn((Camera3dBundle::default(), CameraComponent));

    #[cfg(feature = "debug")]
    {
        e.insert(bevy_flycam::FlyCam);
    }
}
