package com.electricsunstudio.shroudedsun.objects;

import java.util.ArrayList;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.Util;

//watch a set of gameobjects to see when they have been cleared, e.g. check to see when a set of enemies have been defeated.
public class ClearListener
{
	String[] targetNames;
	boolean activated = false;

	/**
	 * 
	 * @param switchNamesConcat the names of the switches to monitor, space separated.
	 */
	public ClearListener(String targetNamesConcat)
	{
		targetNames = targetNamesConcat.split("\\s+");
	}
	public void update()
	{
		activated = Game.inst.gameObjectSystem.allExpired(targetNames);
	}
	
	public boolean isActivated()
	{
		return activated;
	}
}
