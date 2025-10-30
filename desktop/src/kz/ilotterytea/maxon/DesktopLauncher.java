package kz.ilotterytea.maxon;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle(MaxonConstants.GAME_NAME);
        config.setWindowIcon("icons/icon128.png", "icons/icon64.png", "icons/icon32.png", "icons/icon16.png");
        config.setWindowSizeLimits(800, 600, 80000, 60000);

        MaxonGame game = MaxonGame.getInstance();
        game.setDiscordActivity(new DiscordActivityClient());

        new Lwjgl3Application(game, config);
    }
}
