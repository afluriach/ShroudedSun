package com.electricsunstudio.shroudedsun.objects.entity.enemies;

import java.util.LinkedList;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.AI.GuardPatrolFSM;
import com.electricsunstudio.shroudedsun.AI.LookingAround;
import com.electricsunstudio.shroudedsun.AI.LookingAroundFSM;
import com.electricsunstudio.shroudedsun.event.CaughtByGuards;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.RadarSensor;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;
import com.electricsunstudio.shroudedsun.objects.projectile.EnemyBullet;

public class Guard extends Enemy
{
	public static final float radarRadius = 9f;
	RadarSensor radar;
	float fovAngle = 45f;
	
	public Guard(TilespaceRectMapObject mo)
	{
		super(mo, "meiling", 1);
		canDamage(false);
		
		LinkedList<Class<?>> sensing = new LinkedList<Class<?>>();
		sensing.add(Player.class);
		radar = new RadarSensor(getCenterPos(), radarRadius, sensing, "player_sensor");
		
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
		Game.log("caught by guard: " + getName());
//		Game.inst.setDialogMsg("Ha ha ha! Not so fast. Get out of here.");
		Game.inst.setDialog("guards.caught2", "meiling");
		Game.inst.onExitDialog = new CaughtByGuards();
	}
	
	@Override
	public void update()
	{
		super.update();
		
		radar.setPos(getCenterPos());
		radar.update();
		
		//only catch player if she is in the same room as the guard. 		
		if(!radar.getDetectedObjects(getFacingAngle(), fovAngle).isEmpty() &&
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
	
	@Override
	public EnemyBullet getBullet() {
		//doesn't fire
		return null;
	}


}
