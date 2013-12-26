package com.pezventure.map;

import java.util.TreeMap;

public class AreaLoader
{
	private TreeMap<String, Class<?>> areas;
	
	public AreaLoader()
	{
		areas = new TreeMap<String, Class<?>>();
		
		areas.put("level1", Level1.class);
		areas.put("puzzle_room", PuzzleRoom.class);
		areas.put("facer_floor", FacerFloor.class);
		areas.put("meeting_room", MeetingRoom.class);
		areas.put("level_select", LevelSelect.class);
		areas.put("torch_puzzle", TorchPuzzle.class);
	}
	
	public Area loadArea(String name)
	{
		if(!areas.containsKey(name))
			throw new RuntimeException(String.format("area %s not found", name));
		
		return MapUtil.instantiate(areas.get(name));
	}
}
