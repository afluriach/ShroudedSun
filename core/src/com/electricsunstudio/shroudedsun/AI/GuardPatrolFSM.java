package com.electricsunstudio.shroudedsun.AI;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.map.Path;
import com.electricsunstudio.shroudedsun.objects.entity.Entity;

public class GuardPatrolFSM extends AI_FSM
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
	public AI_State getStartState()
	{
		Path path = Game.inst.getCrntArea().getPath(pathName);
		return new FollowPolyline(this, path, speed);
	}

}
