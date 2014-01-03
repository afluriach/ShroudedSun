package com.pezventure;

public class CaughtByGuards implements Runnable
{
	public void run()
	{
		Game.inst.setTeleporDestination("", "player_caught_teleport");
	}
}
