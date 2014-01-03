package com.pezventure.objects.enemies;

import com.pezventure.CaughtByGuards;
import com.pezventure.Game;
import com.pezventure.AI.GuardPatrolFSM;
import com.pezventure.AI.LookingAround;
import com.pezventure.AI.LookingAroundFSM;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.Player;
import com.pezventure.objects.RadarSensor;

public class Guard extends Enemy
{
	public static final float radarRadius = 9f;
	RadarSensor radar;
	float fovAngle = 45f;
	
	public Guard(TilespaceRectMapObject mo)
	{
		super(mo, "meiling", 1);
		
		radar = new RadarSensor(getCenterPos(), radarRadius, Player.class, "player_sensor");
		
		if(mo.prop.containsKey("follow_path"))
		{
			fsm = new GuardPatrolFSM(this, mo.name + "_path", speed);
		}
		else if(mo.prop.containsKey("rotate"))
		{
			float rotateSpeed = Float.parseFloat( mo.prop.get("rotate_speed", String.class));
			
			fsm = new LookingAroundFSM(this, rotateSpeed, mo.prop.get("rotate", String.class).equals("clockwise"));
		}
	}
	
	void catchPlayer()
	{
		Game.log("caught by gaurd: " + getName());
		Game.inst.setDialogMsg("Ha ha ha! Not so fast. Get out of here.");
		Game.inst.onExitDialog = new CaughtByGuards();
	}
	
	@Override
	public void update()
	{
		super.update();
		
		radar.setPos(getCenterPos());
		radar.update();
		
		//only catch player if she is in the same room as the guard. 		
		if(!radar.getDetectedObjects(getDir()*45f, fovAngle).isEmpty() &&
			Game.inst.getCrntArea().getCurrentRoom(getCenterPos()) == Game.inst.getCrntArea().getCurrentRoom(Game.inst.player.getCenterPos()))
		{
			catchPlayer();
		}
	}
	
	@Override
	public void handleContact(GameObject other)
	{
		if(other instanceof Player)
		{
			catchPlayer();
		}
	}

}
