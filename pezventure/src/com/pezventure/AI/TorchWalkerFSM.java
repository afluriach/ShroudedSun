package com.pezventure.AI;

import com.pezventure.objects.Entity;
import com.pezventure.objects.enemies.TorchWalker;

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
