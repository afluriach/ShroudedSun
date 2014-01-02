package com.pezventure.AI;

import com.pezventure.objects.Entity;

public class LookingAroundFSM extends AI_FSM<Entity>
{
	float interval;
	boolean clockwise;
	
	public LookingAroundFSM(Entity agent, float interval, boolean clockwise)
	{
		super(agent);
		this.interval = interval;
		this.clockwise = clockwise;
	}

	@Override
	public AI_State<Entity> getStartState()
	{
		return new LookingAround(this, interval, clockwise);
	}

}
