package com.gensokyouadventure.AI;

import java.util.LinkedList;
import java.util.List;

import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.RadarSensor;
import com.gensokyouadventure.objects.entity.Entity;
import com.gensokyouadventure.objects.entity.Player;
import com.gensokyouadventure.objects.entity.enemies.Enemy;

//this state represents an enemy waiting for the player. 
public class EnemyIdle extends AI_State
{
	private static final float fovAngle = 45f;
	private static final float radarSensorRadius = 7f;
	
	//the list of objects detected by the sensor. 
	List<GameObject> targets;
	
	RadarSensor radar;
	
	public EnemyIdle(AI_FSM fsm)
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
		
		targets = radar.getDetectedObjects(fsm.agent.getFacingAngle(), fovAngle);
		
		if(!targets.isEmpty())
			fsm.changeState(new EnemyAlert(fsm, targets.get(0)));
	}
	

	@Override
	public void onEnter()
	{
		LinkedList<Class<?>> sensing = new LinkedList<Class<?>>();
		sensing.add(Player.class);
		radar = new RadarSensor(fsm.agent.getCenterPos(), radarSensorRadius, sensing, "player_sensor");
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
