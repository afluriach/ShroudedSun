package com.gensokyouadventure.AI;

import com.gensokyouadventure.objects.Entity;
import com.gensokyouadventure.objects.enemies.TorchWalker;

public class TorchWalkerFSM extends AI_FSM<Entity>
{
	public TorchWalkerFSM(TorchWalker agent)
	{
		super(agent);
	}
	
	@Override
	public AI_State<Entity> getStartState()
	{
		return new LookingForTorch(agent, this);
	}

}
