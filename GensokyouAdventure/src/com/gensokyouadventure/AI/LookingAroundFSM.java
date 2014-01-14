package com.gensokyouadventure.AI;

import com.gensokyouadventure.objects.entity.Entity;

public class LookingAroundFSM extends AI_FSM
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
	public AI_State getStartState()
	{
		return new LookingAround(this, interval, clockwise);
	}

}
