package com.electricsunstudio.shroudedsun.AI;

import com.electricsunstudio.shroudedsun.objects.entity.Entity;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.TorchWalker;

public class TorchWalkerFSM extends AI_FSM
{
	public TorchWalkerFSM(TorchWalker agent)
	{
		super(agent);
	}
	
	@Override
	public AI_State getStartState()
	{
		return new LookingForTorch(agent, this);
	}

}
