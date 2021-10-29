package core.gdx.wad.lwjgl3;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import core.gdx.wad.MyGDxTest;
import core.server.ServerGame;

/** Launches the headless server application. */
public class ServerLauncher {
	public static void main(String[] args) {
		try{
			Integer.parseInt(args[0]);
		}catch (Exception e){
			System.out.println("Syntax: java -jar ServerLauncher.jar [tcpPort]");
			System.exit(0);
		}
		createApplication(args);
	}

	private static Application createApplication(String [] args) {
		return new HeadlessApplication(new ServerGame(Integer.parseInt(args[0])), getDefaultConfiguration());
	}

	private static HeadlessApplicationConfiguration getDefaultConfiguration() {
		return new HeadlessApplicationConfiguration();
	}
}