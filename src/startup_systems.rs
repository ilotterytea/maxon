use bevy::prelude::*;

#[allow(unused_mut, unused_variables)]
pub fn spawn_3d_camera(mut commands: Commands) {
    let mut e = commands.spawn(Camera3dBundle::default());

    #[cfg(feature = "debug")]
    {
        e.insert(bevy_flycam::FlyCam);
    }
}
