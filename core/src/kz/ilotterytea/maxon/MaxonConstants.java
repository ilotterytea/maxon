package kz.ilotterytea.maxon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import kz.ilotterytea.maxon.utils.OsUtils;

import java.text.DecimalFormat;

public class MaxonConstants {
    public static final String GAME_NAME = "Maxon Petting Simulator";
    public static final String GAME_APP_ID = "maxon";
    public static final String GAME_APP_PACKAGE = "kz.ilotterytea." + GAME_APP_ID;
    public static final String GAME_APP_URL;
    public static final String GAME_VERSION = "1.0.0";
    public static final String GAME_MAIN_DEVELOPER = "ilotterytea";

    public static final String[][] GAME_DEVELOPERS = {
            {"ilotterytea", "https://ilotterytea.kz"},
            {"greddyss", "https://twitch.tv/greddyss"},
            {"enotegg", "https://twitch.tv/enotegg"},
            {"namesake", "https://twitter.com/nameisnamesake"},
            {"saopin", "https://twitch.tv/saopin_"},
            {"gvardovskiy", "https://twitch.tv/gvardovskiy"}
    };

    public static final String GAME_MAIN_FOLDER = OsUtils.getUserDataDirectory(GAME_MAIN_DEVELOPER + "/" + GAME_APP_ID);
    public static final String GAME_SCREENSHOT_FOLDER = GAME_MAIN_FOLDER + "/screenshots";
    public static final String GAME_SAVEGAME_FOLDER = GAME_MAIN_FOLDER + "/savegames";

    public static final FileHandle FILE_EN_US = Gdx.files.internal("i18n/en_us.json");
    public static final FileHandle FILE_RU_RU = Gdx.files.internal("i18n/ru_ru.json");

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.#");
    public static final DecimalFormat DECIMAL_FORMAT2 = new DecimalFormat("###,###");

    public static final String GAME_VERSIONS_FILE_URL = "https://assets.ilotterytea.kz/maxon/versions.json";

    public static final long DISCORD_APPLICATION_ID = 1051092609659052062L;

    public static final Texture MISSING_TEXTURE;

    static {
        Pixmap pixmap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);

        int checkers = 2;
        int tileSize = pixmap.getWidth() / checkers;

        for (int y = 0; y < checkers; y++) {
            for (int x = 0; x < checkers; x++) {
                if ((x + y) % 2 == 0){
                    pixmap.setColor(Color.MAGENTA);
                } else {
                    pixmap.setColor(Color.BLACK);
                }

                pixmap.fillRectangle(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

        MISSING_TEXTURE = new Texture(pixmap);
        pixmap.dispose();

        if (OsUtils.isAndroid) {
            GAME_APP_URL = "https://play.google.com/store/apps/details?id=kz.ilotterytea.maxon";
        } else {
            GAME_APP_URL = "https://ilotterytea.itch.io/maxon";
        }
    }
}
