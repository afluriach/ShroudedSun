package com.pezventure.AI;

import com.pezventure.objects.GameObject;

/**
 * Describes a state in an AI FSM. 
 * @author ant
 *
 */
public abstract class AI_State<AgentType extends GameObject>
{
	AI_FSM<AgentType> fsm;
	
	public AI_State(AI_FSM<AgentType> fsm)
	{
		this.fsm = fsm;
	}
	
	public abstract void update();
	public abstract void onEnter();
	public abstract void onExit();
}
