package com.electricsunstudio.shroudedsun.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.electricsunstudio.shroudedsun.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Shrouded Sun";
		config.width = Game.DEFAULT_SCREEN_WIDTH;
		config.height = Game.DEFAULT_SCREEN_HEIGHT;
		
		new LwjglApplication(new Game(false), config);
	}
}
