package com.gensokyouadventure.map;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
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
	
	public String[] getAreas()
	{
		return areas.toArray(new String[areas.size()]);
	}
	
	public String[] getLinksInArea(String name)
	{
		if(!areas.contains(name))
			throw new RuntimeException(String.format("area %s not found", name));
		
		TiledMap areaMap = MapUtil.loadMap(String.format("maps/%s.tmx", name));
		MapLayer maplinkLayer = areaMap.getLayers().get("maplinks");
		
		String[] linkNames = new String[maplinkLayer.getObjects().getCount()];
		
		for(int i=0;i<maplinkLayer.getObjects().getCount(); ++i)
		{
			linkNames[i] = maplinkLayer.getObjects().get(i).getName();
		}
		
		return linkNames;
	}
}
