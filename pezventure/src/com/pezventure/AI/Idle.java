package com.pezventure.AI;

import com.pezventure.objects.Entity;

public class Idle extends AI_State<Entity>
{
	public Idle(AI_FSM<Entity> fsm)
	{
		super("Idle", fsm);
	}

	@Override
	public void update()
	{
	}
	

	@Override
	public void onEnter()
	{
		
	}

	@Override
	public void onExit()
	{
		
	}
}
