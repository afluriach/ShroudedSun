package com.gensokyouadventure.map;

import java.util.HashSet;

//class for storing all persistent state associated with an area
public class AreaState
{
	public HashSet<String> activatedObjects = new HashSet<String>();
	public HashSet<String> openedChests = new HashSet<String>();
}
