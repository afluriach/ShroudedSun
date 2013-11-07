package com.pezventure.client;

import com.pezventure.Game;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(Game.DEFAULT_SCREEN_WIDTH, Game.DEFAULT_SCREEN_HEIGHT);
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return new Game(false);
	}
}