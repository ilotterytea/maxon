package kz.ilotterytea.maxon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import kz.ilotterytea.maxon.MaxonConstants;

import java.io.File;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.Deflater;

public class ScreenshotFactory {
    /**
     * Take a screenshot.
     * Default without any compression. Y is flipped.
     */
    public static void takeScreenshot(){
        _takeScreenshot(Deflater.NO_COMPRESSION, true);
    }

    /**
     * Take a screenshot. It will be saved in the user data directory, it is different for each platform (Windows: %appdata%/.maxoning/screenshots/, Linux: ~/.local/share/maxoning/screenshots).
     */
    public static void takeScreenshot(int compression, boolean flipY){
        _takeScreenshot(compression, flipY);
    }

    private static void _takeScreenshot(int compression, boolean flipY) {
        File file = new File(MaxonConstants.GAME_SCREENSHOT_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd-HHmmss");
        LocalDateTime now = LocalDateTime.now();
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        ByteBuffer pixels = pixmap.getPixels();

        int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;
        for (int i = 3; i < size; i += 4) {
            pixels.put(i, (byte) 255);
        }

        PixmapIO.writePNG(new FileHandle(file.getPath() + String.format("/screenshot-%s.png", dtf.format(now))), pixmap, compression, flipY);
        pixmap.dispose();
    }
}