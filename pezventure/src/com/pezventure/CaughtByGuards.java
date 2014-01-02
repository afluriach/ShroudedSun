package com.pezventure;

public class CaughtByGuards implements GameEvent
{
	public void execute()
	{
		Game.inst.teleport("", "player_caught_teleport");
	}
}
