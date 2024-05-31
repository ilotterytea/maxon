package kz.ilotterytea.maxon.utils.serialization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Null;
import com.google.gson.Gson;
import kz.ilotterytea.maxon.MaxonConstants;
import kz.ilotterytea.maxon.player.MaxonSavegame;
import kz.ilotterytea.maxon.utils.OsUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

/**
 * External game data system control.
 * @author NotDankEnough
 * @since Alpha 1.0
 */
public class GameDataSystem {
    private static final File dir = new File(MaxonConstants.GAME_SAVEGAME_FOLDER);
    private static final Gson gson = new Gson();
    private static final Logger log = LoggerFactory.getLogger(GameDataSystem.class.getSimpleName());

    /**
     * Get all savefiles from savegame directory (/.Maxoning/savegames/)
     * @return Array of MaxonSavegames
     * @see MaxonSavegame
     */
    public static ArrayList<MaxonSavegame> getSavegames() {
        if (!dir.exists()) dir.mkdirs();
        
        ArrayList<MaxonSavegame> saves = new ArrayList<>();
        File[] files = dir.listFiles();

        assert files != null;
        for (File file : files) {
            try {

                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);

                MaxonSavegame sav = gson.fromJson(ois.readUTF(), MaxonSavegame.class);
                saves.add(sav);

                ois.close();
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return saves;
    }

    /**
     * Convert <b>MaxonSavegame</b> class to <b>JSON</b> string and write in UTF-8 encoding (I'm sorry, encryption enjoyers).
     * @param savegame Save game object.
     * @param file_name File name.
     * @see MaxonSavegame
     */
    public static void save(@NotNull MaxonSavegame savegame, @NotNull String file_name) {
        if (!dir.exists()) dir.mkdirs();

        try {
            log.info("Saving the game...");
            FileOutputStream fos = new FileOutputStream(String.format("%s/%s", (OsUtils.isAndroid || OsUtils.isIos) ? Gdx.files.getExternalStoragePath() : dir.getAbsolutePath(), file_name));
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeUTF(gson.toJson(savegame));
            oos.close();
            log.info(String.format("Success! Savegame located at %s/%s", (OsUtils.isAndroid || OsUtils.isIos) ? Gdx.files.getExternalStoragePath() : dir.getAbsolutePath(), file_name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reading a <b>JSON</b> string from the file and convert to <b>MaxonSavegame</b> class.
     * @param file_name File name. If null - it will get the first file by last modified time.
     * @return Filled <b>MaxonSavegame</b> class
     * @see MaxonSavegame
     */
    public static MaxonSavegame load(@Null String file_name) {
        MaxonSavegame sav = null;

        if (new File(dir.getAbsolutePath() + "/" + file_name).exists() && !(OsUtils.isAndroid || OsUtils.isIos)) {
            try {
                log.info(String.format("Trying to get the savegame at %s/%s...", dir.getAbsolutePath(), file_name));
                FileInputStream fis = new FileInputStream(String.format("%s/%s", dir.getAbsolutePath(), file_name));
                ObjectInputStream oos = new ObjectInputStream(fis);

                sav = gson.fromJson(oos.readUTF(), MaxonSavegame.class);
                oos.close();

                log.info(String.format("Successfully loaded the savegame from %s/%s!", dir.getAbsolutePath(), file_name));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if ((OsUtils.isAndroid || OsUtils.isIos) && new File(Gdx.files.getExternalStoragePath() + file_name).exists()) {
            try {
                log.info(String.format("Trying to get the savegame at %s%s...", Gdx.files.getExternalStoragePath(), file_name));
                FileInputStream fis = new FileInputStream(String.format("%s%s", Gdx.files.getExternalStoragePath(), file_name));
                ObjectInputStream oos = new ObjectInputStream(fis);

                sav = gson.fromJson(oos.readUTF(), MaxonSavegame.class);
                oos.close();

                log.info(String.format("Successfully loaded the savegame from %s%s!", Gdx.files.getExternalStoragePath(), file_name));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return sav;
    }
}
