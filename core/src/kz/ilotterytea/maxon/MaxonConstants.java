package kz.ilotterytea.maxon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import kz.ilotterytea.maxon.utils.OsUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class MaxonConstants {
    public static final String GAME_NAME = "Maxon Petting Simulator";
    public static final String GAME_APP_ID = "maxon";
    public static final String GAME_VERSION = "Alpha 1.2";
    public static final String GAME_GHTAG = "alpha-1.2";
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

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.##");
    public static final DecimalFormat DECIMAL_FORMAT2 = new DecimalFormat("###,###");
    @SuppressWarnings("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
    public static final long startTime = System.currentTimeMillis();

}
