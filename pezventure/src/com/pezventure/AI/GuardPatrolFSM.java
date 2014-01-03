package com.pezventure.AI;

import com.pezventure.Game;
import com.pezventure.map.Path;
import com.pezventure.objects.Entity;

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
