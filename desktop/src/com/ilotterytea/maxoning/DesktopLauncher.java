package com.ilotterytea.maxoning;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) throws FileNotFoundException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Maxon Petting Simulator");
		config.setWindowIcon("icon.png");
		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		new Lwjgl3Application(new MaxonGame(), config);
	}

	private static String getRandomLine() throws FileNotFoundException {
		Scanner scan = new Scanner(new File("texts/splashes.txt"));
		ArrayList<String> strings = new ArrayList<>();

		while (scan.hasNext()) {
			strings.add(scan.next());
		}

		return strings.get((int) Math.floor(Math.random() * strings.size()));
	}

}
