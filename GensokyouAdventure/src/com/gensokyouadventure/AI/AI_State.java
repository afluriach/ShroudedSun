package com.gensokyouadventure.AI;

import com.gensokyouadventure.objects.GameObject;

/**
 * Describes a state in an AI FSM. 
 * @author ant
 *
 */
public abstract class AI_State
{
	AI_FSM fsm;
	
	public AI_State(AI_FSM fsm)
	{
		this.fsm = fsm;
	}
	
	public abstract void update();
	public abstract void onEnter();
	public abstract void onExit();
}
