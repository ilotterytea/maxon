package kz.ilotterytea.maxon.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import kz.ilotterytea.maxon.assets.loaders.Text;
import kz.ilotterytea.maxon.assets.loaders.TextLoader;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.List;
import java.util.stream.Collectors;

public class AssetUtils {
    public static void setup(AssetManager assetManager) {
        assetManager.setLoader(SceneAsset.class, ".glb", new GLBAssetLoader());
        assetManager.setLoader(Text.class, new TextLoader(new InternalFileHandleResolver()));
    }

    public static void queue(AssetManager assetManager) {
        FileHandle assetsFile = Gdx.files.internal("assets.txt");
        String contents = assetsFile.readString();
        String[] filePaths = contents.split("\n");

        for (String filePath : filePaths) {
            String[] splitFilePath = filePath.split("/");
            String[] splitFileName = splitFilePath[splitFilePath.length - 1].split("\\.");
            String extension = splitFileName[splitFileName.length - 1];

            Class<?> type = null;

            switch (extension) {
                case "png":
                    type = Texture.class;
                    break;
                case "atlas":
                    type = TextureAtlas.class;
                    break;
                case "skin":
                    type = Skin.class;
                    break;
                case "json":
                case "txt":
                    type = Text.class;
                    break;
                case "glb":
                    type = SceneAsset.class;
                    break;
                case "wav":
                case "mp3":
                    type = Music.class;
                    break;
                case "ogg":
                    type = Sound.class;
                    break;
                default:
                    break;
            }

            if (type == null) {
                continue;
            }

            assetManager.load(filePath, type);
        }
    }
}