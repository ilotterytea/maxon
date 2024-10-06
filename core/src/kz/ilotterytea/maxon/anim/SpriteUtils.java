package kz.ilotterytea.maxon.anim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class SpriteUtils {
    public static ArrayList<TextureRegion> splitToTextureRegions(
            Texture texture,
            int tileWidth,
            int tileHeight,
            int columns,
            int rows
    ) {
        TextureRegion[][] tmp = TextureRegion.split(texture, tileWidth, tileHeight);

        ArrayList<TextureRegion> frames = new ArrayList<>();

        int index = 0;

        for (TextureRegion[] regArray : tmp) {
            for (TextureRegion reg : regArray) {
                if (reg != null) {
                    frames.add(reg);
                }
            }
        }

        return frames;
    }
}
