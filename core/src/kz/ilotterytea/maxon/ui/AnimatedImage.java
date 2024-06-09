package kz.ilotterytea.maxon.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

public class AnimatedImage extends Image implements Disposable {
    private final TextureRegion[] regions;
    private final int fps;
    private int index = 0, seconds = 0;

    private boolean stopAnim = false;

    public AnimatedImage(TextureRegion[] regions) {
        super(regions[0]);
        this.regions = regions;
        this.fps = 0;
    }

    public AnimatedImage(TextureRegion[] regions, int fps) {
        super(regions[0]);
        this.regions = regions;
        this.fps = fps;
    }

    @Override public void act(float delta) {
        if (!stopAnim && seconds >= fps) {
            if (index > regions.length - 1) {
                index = 0;
            }
            try {
                if (regions[index + 1] == null) {
                    index = 0;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {}

            super.setDrawable(new TextureRegionDrawable(regions[index]));
            index++;
            seconds = 0;
        }

        seconds++;
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
