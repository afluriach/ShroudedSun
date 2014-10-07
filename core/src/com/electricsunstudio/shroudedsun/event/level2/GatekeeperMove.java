package com.electricsunstudio.shroudedsun.event.level2;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.Gatekeeper;

public class GatekeeperMove implements Runnable 
{
	@Override
	public void run()
	{
		Game.inst.gameObjectSystem.getObjectByName("gatekeeper", Gatekeeper.class).move();
	}
	
}
