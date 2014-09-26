package com.electricsunstudio.shroudedsun.map;

import com.badlogic.gdx.math.Rectangle;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;

public class MapLink
{
	public String name;
	public String destMap;
	public String destLink;
	public PrimaryDirection entranceDir;
	public Rectangle location;
	
	public boolean isExit()
	{
		return destMap != null && destLink != null;
	}
	
}
