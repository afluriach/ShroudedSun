package com.electricsunstudio.shroudedsun.AI;

import com.badlogic.gdx.Gdx;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.entity.Entity;
import com.electricsunstudio.shroudedsun.objects.environment.Torch;

//this state will be entered when the torch walker finishes the path.
//light the target torch and then loop back to looking for torch
public class LightTorch extends AI_State
{
	Torch target;
	public LightTorch(AI_FSM fsm, Torch target)
	{
		super(fsm);
		this.target = target;
	}

	@Override
	public void update()
	{
		target.light();
		fsm.changeState(new LookingForTorch(fsm.agent, fsm));
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
