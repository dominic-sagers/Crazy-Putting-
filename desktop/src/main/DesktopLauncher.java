package main;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import menu.MenuController;
import testing.TestMap;

import java.io.IOException;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
/**
 * This class is the entry point for the application. Run the main method in this class to start the application.
 */
public class DesktopLauncher {

	public static void main(String[] arg) throws IOException {

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("CrazyPutting - Project Manhattan");
		config.setWindowedMode(1200, 800);
		config.setResizable(false);

		new Lwjgl3Application(new MenuController(), config);
		TestMap.test();
	}
}
