use bevy::prelude::*;
use bevy_sprite3d::TextureAtlas3dData;

#[derive(Component)]
pub struct AnimationTimer(pub Timer);

pub struct TextureAtlasAnimationPlugin;

impl Plugin for TextureAtlasAnimationPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(Update, (update_texture_atlases, update_3d_sprites));
    }
}

fn update_texture_atlases(
    time: Res<Time>,
    mut query: Query<(&mut AnimationTimer, &mut TextureAtlas)>,
    texture_atlas_layout_assets: Res<Assets<TextureAtlasLayout>>,
) {
    for (mut timer, mut atlas) in query.iter_mut() {
        timer.0.tick(time.delta());

        if timer.0.just_finished() {
            if let Some(layout) = texture_atlas_layout_assets.get(atlas.layout.id()) {
                atlas.index = (atlas.index + 1) % layout.textures.len();
            }
        }
    }
}

fn update_3d_sprites(
    time: Res<Time>,
    mut query: Query<(&mut AnimationTimer, &mut TextureAtlas, &TextureAtlas3dData)>,
) {
    for (mut timer, mut atlas, data) in query.iter_mut() {
        timer.0.tick(time.delta());

        if timer.0.just_finished() {
            atlas.index = (atlas.index + 1) % data.keys.len();
        }
    }
}
