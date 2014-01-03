package com.gensokyouadventure.map;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

public class TilespaceRectMapObject
{
	public Rectangle rect;
	
	public String name;
	public String type;
	
	public MapProperties prop;
	
	public TilespaceRectMapObject(RectangleMapObject mo, int mapHeightPixels)
	{
		prop = mo.getProperties();
		
		rect = MapUtil.tilespaceRect(mo, mapHeightPixels);
		name = mo.getName();
		type = prop.get("type", String.class);
	}
}
