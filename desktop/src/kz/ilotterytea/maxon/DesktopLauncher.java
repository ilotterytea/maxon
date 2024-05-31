package kz.ilotterytea.maxon;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle(String.format("%s %s: %s", MaxonConstants.GAME_NAME, MaxonConstants.GAME_VERSION, getRandomLine()));
		config.setWindowIcon("icon_chest.png");

		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

		new Lwjgl3Application(new MaxonGame(), config);
	}

	private static String getRandomLine() {
		String line = "missingno";

		try {
			Scanner scanner = new Scanner(
					Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("texts/splashes.txt"))
			);
			ArrayList<String> strings = new ArrayList<>();

			while (scanner.hasNext()) {
				strings.add(scanner.next());
			}

			line = strings.get((int) Math.floor(Math.random() * strings.size()));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return line;
	}
}
