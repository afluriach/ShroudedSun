package com.gensokyouadventure.map;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import com.badlogic.gdx.files.FileHandle;
import com.gensokyouadventure.Util;

public class AreaLoader
{
	private Set<String> areas;
	
	public AreaLoader()
	{
		areas = new HashSet<String>();
		
		FileHandle dir = Util.getInternalDirectory("maps/");
		
		for(FileHandle fh : dir.list())
		{
			if(fh.extension().equals("tmx"))
			{
				areas.add(fh.nameWithoutExtension());
			}
		}		
	}
	
	public Area loadArea(String name)
	{
		if(!areas.contains(name))
			throw new RuntimeException(String.format("area %s not found", name));
		
		return new Area(MapUtil.loadMap(String.format("maps/%s.tmx", name)));
	}
}
