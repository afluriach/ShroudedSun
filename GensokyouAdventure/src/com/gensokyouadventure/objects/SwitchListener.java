package com.gensokyouadventure.objects;

import java.util.ArrayList;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.Util;

//monitor a set of switch objects in game (given by name) and check for activation
public class SwitchListener
{
	String[] switchNames;
	ArrayList<Switch> switches;
	boolean activated = false;

	/**
	 * 
	 * @param switchNamesConcat the names of the switches to monitor, space separated.
	 */
	public SwitchListener(String switchNamesConcat)
	{
		switchNames = switchNamesConcat.split("\\s+");
	}
	
	public void init()
	{
		switches = new ArrayList<Switch>(switchNames.length);
		
		for(int i=0;i<switchNames.length; ++i)
		{
			switches.add((Switch) Game.inst.gameObjectSystem.getObjectByName(switchNames[i]));
		}

	}
	
	public void update()
	{
		activated = Util.allActivated(switches);
	}
	
	public boolean isActivated()
	{
		return activated;
	}
}
