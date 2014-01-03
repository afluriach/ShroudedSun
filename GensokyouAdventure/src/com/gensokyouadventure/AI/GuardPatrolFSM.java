package com.gensokyouadventure.AI;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.map.Path;
import com.gensokyouadventure.objects.Entity;

public class GuardPatrolFSM extends AI_FSM<Entity>
{
	String pathName;
	float speed;
	
	public GuardPatrolFSM(Entity agent, String path, float speed)
	{
		super(agent);
		this.pathName = path;
		this.speed = speed;
	}

	@Override
	public AI_State<Entity> getStartState()
	{
		Path path = Game.inst.getCrntArea().getPath(pathName);
		return new FollowPolyline(this, path, speed);
	}

}
