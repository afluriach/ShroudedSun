package com.pezventure.map;

import com.badlogic.gdx.math.Rectangle;
import com.pezventure.physics.PrimaryDirection;

public class MapLink
{
	String destMap;
	String destLink;
	PrimaryDirection entranceDir;
	Rectangle location;
	
	public boolean isExit()
	{
		return destMap != null && destLink != null;
	}
	
}
