package com.pezventure.AI;

import com.pezventure.objects.Entity;

public class BlueEnemyFSM extends AI_FSM<Entity>
{
	public BlueEnemyFSM(Entity agent)
	{
		super(agent);
	}

	@Override
	public AI_State<Entity> getStartState()
	{
		// TODO Auto-generated method stub
		return new BlueEnemyIdle(this);
	}

}
