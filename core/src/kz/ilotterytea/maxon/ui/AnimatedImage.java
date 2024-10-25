package kz.ilotterytea.maxon.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;

public class AnimatedImage extends Image implements Disposable {
    private final ArrayList<TextureRegion> regions;
    private final int fps;
    private int index = 0, seconds = 0;

    private final boolean stopAnim = false;

    public AnimatedImage(ArrayList<TextureRegion> regions, int fps) {
        super(regions.get(0));
        this.regions = regions;
        this.fps = fps;
    }

    @Override public void act(float delta) {
        if (!stopAnim && seconds >= fps) {
            if (index > regions.size() - 1) {
                index = 0;
            }
            try {
                if (regions.get(index + 1) == null) {
                    index = 0;
                }
            } catch (IndexOutOfBoundsException ignored) {}

            super.setDrawable(new TextureRegionDrawable(regions.get(index)));
            index++;
            seconds = 0;
        }

        seconds++;
        super.act(delta);
    }

    public TextureRegion getFrame(int index) { return regions.get(index); }

    @Override public void dispose() {
        for (TextureRegion reg : regions) {
            if (reg != null) {
                reg.getTexture().dispose();
            }
        }
    }
}
