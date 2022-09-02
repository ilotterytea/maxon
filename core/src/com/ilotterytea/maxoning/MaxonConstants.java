package com.ilotterytea.maxoning;

import com.ilotterytea.maxoning.utils.OsUtils;

public class MaxonConstants {
    public static final String GAME_NAME = "Maxon Petting Simulator";
    public static final String GAME_VERSION = "Alpha 1.0.1";
    public static final String GAME_PUBLISHER = "iLotterytea";

    public static final String GAME_MAIN_FOLDER = OsUtils.getUserDataDirectory(".Maxoning");
    public static final String GAME_SCREENSHOT_FOLDER = GAME_MAIN_FOLDER + "/screenshots";
    public static final String GAME_SAVEGAME_FOLDER = GAME_MAIN_FOLDER + "/savegames";
}
