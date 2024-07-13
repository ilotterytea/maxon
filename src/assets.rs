use bevy::prelude::*;
use bevy_asset_loader::prelude::*;

#[derive(AssetCollection, Resource)]
pub struct ModelAssets {
    #[asset(path = "models/scenes/living_room.glb#Scene0")]
    pub living_room: Handle<Scene>,
}
