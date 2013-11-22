package com.pezventure.map;

import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.pezventure.Game;

public class AreaLoader
{
	private TreeMap<String, Class<?>> areas;
	
	public AreaLoader()
	{
		areas = new TreeMap<String, Class<?>>();
		
		areas.put("level1", Level1.class);
		areas.put("puzzle_room", PuzzleRoom.class);
		areas.put("facer_floor", FacerFloor.class);
		
	}
	
	public Area loadArea(String name)
	{
		if(!areas.containsKey(name))
			throw new RuntimeException(String.format("area %s not found", name));
		
		return MapUtil.instantiate(areas.get(name));
	}
}
