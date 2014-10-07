package com.electricsunstudio.shroudedsun.AI;

/**
 *
 * @author toni
 */
public abstract class NPC_State extends AI_State
{
	public NPC_State(AI_FSM fsm) {
		super(fsm);
	}
	public abstract boolean canTalk();
	public abstract String getDialog();
}
