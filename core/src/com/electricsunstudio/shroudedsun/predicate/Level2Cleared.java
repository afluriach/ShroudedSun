package com.electricsunstudio.shroudedsun.predicate;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.Level2Sensor;
import java.util.List;

public class Level2Cleared implements Predicate
{
	List<Level2Sensor> sensors;
	
	public Level2Cleared()
	{
		sensors = Game.inst.gameObjectSystem.getObjectsByType(Level2Sensor.class);
	}
	
	@Override
	public boolean test()
	{
		int total_contained = 0;
		
		for(Level2Sensor sensor : sensors)
		{
			total_contained += sensor.getOccupancy();
		}
		
		return total_contained == 10;		
	}
}
