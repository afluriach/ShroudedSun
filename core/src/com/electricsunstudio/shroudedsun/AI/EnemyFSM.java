package com.electricsunstudio.shroudedsun.AI;

import com.electricsunstudio.shroudedsun.objects.entity.Entity;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Enemy;

public class EnemyFSM extends AI_FSM
{
	public EnemyFSM(Enemy agent)
	{
		super(agent);
	}

	@Override
	public AI_State getStartState()
	{
		// TODO Auto-generated method stub
		return new EnemyIdle(this);
	}

}
