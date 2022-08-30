package com.ilotterytea.maxoning.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

public class AnimatedImage extends Image implements Disposable {
    private final TextureRegion[] regions;
    private int index = 0;

    private boolean stopAnim = false;

    public AnimatedImage(TextureRegion[] regions) {
        super(regions[0]);
        this.regions = regions;
    }

    @Override public void act(float delta) {
        if (!stopAnim) {
            if (index > regions.length - 1) {
                index = 0;
            }
            if (regions[index + 1] == null) {
                index = 0;
            }
            super.setDrawable(new TextureRegionDrawable(regions[index]));
            index++;
        }
        super.act(delta);
    }

    public TextureRegion getFrame(int index) { return regions[index]; }
    public int getIndex() { return index; }
    public Drawable getDrawable() { return super.getDrawable(); }

    public void nextFrame() {
        index++;

        if (index > regions.length - 1 || regions[index] == null) {
            index = 0;
        }

        super.setDrawable(new TextureRegionDrawable(regions[index]));
    }

    public void disableAnim() { stopAnim = true; }
    public void enableAnim() { stopAnim = false; }

    public boolean isAnimationStopped() { return stopAnim; }

    @Override public void dispose() {
        for (TextureRegion reg : regions) {
            if (reg != null) {
                reg.getTexture().dispose();
            }
        }
    }
}
