package com.electricsunstudio.shroudedsun.AI;

import com.electricsunstudio.shroudedsun.objects.entity.Entity;

public class RandomWalkFSM extends AI_FSM {

	public RandomWalkFSM(Entity agent) {
		super(agent);
	}

	@Override
	public AI_State getStartState() {
		return new RandomWalk(this,1,3);
	}
	
}
