package kz.ilotterytea.maxon.player.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import kz.ilotterytea.maxon.anim.SpriteUtils
import kz.ilotterytea.maxon.ui.AnimatedImage

/**
 * Utilities for some operations with pets.
 */
class PetUtils {
    companion object {
        @JvmStatic
                /**
                 * Get animated image of pet by its ID.
                 * */
        fun animatedImageById(assetManager: AssetManager, id: Int) : AnimatedImage {
            val img: AnimatedImage

            when (id) {
                // Maxon:
                0 -> img = AnimatedImage(SpriteUtils.splitToTextureRegions(
                    assetManager.get(
                        "sprites/sheet/loadingCircle.png",
                        Texture::class.java
                    ),
                    112, 112, 10, 5
                ))
                // Maxon:
                else -> img = AnimatedImage(SpriteUtils.splitToTextureRegions(
                    assetManager.get(
                        "sprites/sheet/loadingCircle.png",
                        Texture::class.java
                    ),
                    112, 112, 10, 5
                ))
            }

            return img
        }
    }
}