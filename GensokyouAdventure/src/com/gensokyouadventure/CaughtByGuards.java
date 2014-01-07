package com.gensokyouadventure;


public class CaughtByGuards implements Runnable
{
	public void run()
	{
		Game.inst.traverseLink("", "player_caught_teleport");
	}
}
