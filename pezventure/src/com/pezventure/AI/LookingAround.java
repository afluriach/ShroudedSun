package com.pezventure.AI;

import com.pezventure.Game;
import com.pezventure.objects.Entity;

public class LookingAround extends AI_State<Entity>
{
	float timeInterval;
	boolean clockwise;
	
	float timeAccumulated = 0;
	
	public LookingAround(AI_FSM<Entity> fsm, float timeInterval, boolean clockwise)
	{
		super(fsm);
		this.clockwise = clockwise;
		this.timeInterval = timeInterval;
	}

	@Override
	public void update() {
		timeAccumulated += Game.SECONDS_PER_FRAME;
		
		if(timeAccumulated > timeInterval)
		{
			timeAccumulated = 0;
			AI_Util.rotate(clockwise, fsm.agent);
		}
	}

	@Override
	public void onEnter() {
	}

	@Override
	public void onExit() {

	}

}
