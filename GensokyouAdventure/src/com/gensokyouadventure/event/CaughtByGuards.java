package com.gensokyouadventure.event;

import com.gensokyouadventure.Game;


public class CaughtByGuards implements Runnable
{
	public void run()
	{
		Game.inst.traverseLink("", "player_caught_teleport");
	}
}
