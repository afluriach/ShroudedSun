package com.pezventure.AI;

import com.pezventure.objects.GameObject;

/**
 * Describes a state in an AI FSM. 
 * @author ant
 *
 */
public abstract class AI_State<AgentType extends GameObject>
{
	String name;
	AI_FSM<AgentType> fsm;
	
	public AI_State(String name, AI_FSM<AgentType> fsm)
	{
		this.name = name;
		this.fsm = fsm;
	}
	
	public abstract void update();
	public abstract void onEnter();
	public abstract void onExit();
}
