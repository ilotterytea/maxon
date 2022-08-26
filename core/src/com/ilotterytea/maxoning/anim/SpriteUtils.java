package com.ilotterytea.maxoning.anim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;

public class SpriteUtils {
    public static TextureRegion[] splitToTextureRegions(
            Texture texture,
            int tileWidth,
            int tileHeight,
            int columns,
            int rows
    ) {
        TextureRegion[][] tmp = TextureRegion.split(texture, tileWidth, tileHeight);
        TextureRegion[] frames = new TextureRegion[(texture.getWidth() / columns) + (texture.getHeight() / rows)];

        int index = 0;

        for (TextureRegion[] regArray : tmp) {
            for (TextureRegion reg : regArray) {
                if (reg != null) {
                    frames[index++] = reg;
                }
            }
        }

        System.out.println(Arrays.deepToString(tmp));
        System.out.println(frames.length);

        return frames;
    }
}
