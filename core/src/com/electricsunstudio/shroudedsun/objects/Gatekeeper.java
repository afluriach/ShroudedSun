package com.electricsunstudio.shroudedsun.objects;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.entity.StationaryNPC;
import java.util.List;
import com.electricsunstudio.shroudedsun.objects.Level2Sensor;

public class Gatekeeper extends StationaryNPC
{
	List<Level2Sensor> sensors;
	
    public Gatekeeper(TilespaceRectMapObject to) {
        super(to, "komachi");
    }

	boolean levelClear()
	{
		int total_contained = 0;
		
		for(Level2Sensor sensor : sensors)
		{
			total_contained += sensor.getOccupancy();
		}
		
		return total_contained == 10;
	}
	
	@Override
	public void update()
	{
		activated = activated ||  levelClear();
		
		super.update();
	}
	
	@Override
	public void init()
	{
		sensors = Game.inst.gameObjectSystem.getObjectsByType((Level2Sensor.class));
	}
}
