package com.ilotterytea.maxoning.anim;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimatedImage extends Image {
    private float stateTime = 0;
    private final TextureRegion[] regions;
    private int index = 0;

    public AnimatedImage(TextureRegion[] regions) {
        super(regions[0]);
        this.regions = regions;
    }

    @Override public void act(float delta) {
        if (index > regions.length - 1) {
            index = 0;
        }
        if (regions[index + 1] == null) {
            index = 0;
        }
        super.setDrawable(new TextureRegionDrawable(regions[index]));
        index++;
        super.act(delta);

    }

    public void dispose() {
        for (TextureRegion reg : regions) {
            if (reg != null) {
                reg.getTexture().dispose();
            }
        }
    }
}
