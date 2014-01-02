package com.pezventure.AI;

import java.util.List;

import com.pezventure.objects.Entity;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.Player;
import com.pezventure.objects.RadarSensor;

//this state represents an enemy waiting for the player. 
public class BlueEnemyIdle extends AI_State<Entity>
{
	private static final float fovAngle = 45f;
	private static final float radarSensorRadius = 7f;
	
	//the list of objects detected by the sensor. 
	List<GameObject> targets;
	
	RadarSensor radar;
	
	public BlueEnemyIdle(AI_FSM<Entity> fsm)
	{
		super(fsm);
	}

	@Override
	public void update()
	{
//		System.out.println("radar: " + radar);
//		System.out.println("agent: " + fsm.agent);
		
		//no gaurantee that the entity is stationary. it may be moved externally or 
		//a state may subclass Idle to include movement
		radar.setPos(fsm.agent.getCenterPos());
		
		targets = radar.getDetectedObjects(fsm.agent.getDir()*45f, fovAngle);
		
		if(!targets.isEmpty())
			fsm.changeState(new BlueEnemyAlert(fsm, targets.get(0)));
	}
	

	@Override
	public void onEnter()
	{
		radar = new RadarSensor(fsm.agent.getCenterPos(), radarSensorRadius, Player.class, "player_sensor");
	}

	@Override
	public void onExit()
	{
		radar.expire();
		radar = null;
	}
	
	public List<GameObject> getTargets() {
		return targets;
	}

}
