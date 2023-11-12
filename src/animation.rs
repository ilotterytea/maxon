use bevy::{prelude::*, time::Timer};

#[derive(Component, Clone)]
pub struct Animation {
    pub timer: Timer,
    pub frame_count: usize,
    pub is_active: bool,
}

impl Animation {
    pub fn update(&mut self, delta_time: &Res<Time>, index: &mut usize) {
        self.timer.tick(delta_time.delta());

        if self.timer.just_finished() {
            *index += 1;

            if *index >= self.frame_count {
                *index = 0;
            }
        }
    }
}

pub fn update_animations(
    delta_time: Res<Time>,
    mut tas_query: Query<(&mut TextureAtlasSprite, &mut Animation), Without<UiTextureAtlasImage>>,
    mut utas_query: Query<(&mut UiTextureAtlasImage, &mut Animation), Without<TextureAtlasSprite>>,
) {
    for (mut s, mut a) in tas_query.iter_mut() {
        if !a.is_active {
            continue;
        }

        a.update(&delta_time, &mut s.index);
    }

    for (mut s, mut a) in utas_query.iter_mut() {
        if !a.is_active {
            continue;
        }

        a.update(&delta_time, &mut s.index);
    }
}
