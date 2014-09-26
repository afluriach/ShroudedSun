package com.electricsunstudio.shroudedsun.event;

import com.electricsunstudio.shroudedsun.Game;


public class CaughtByGuards implements Runnable
{
	public void run()
	{
		Game.inst.traverseLink("", "player_caught_teleport");
	}
}
