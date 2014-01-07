package com.gensokyouadventure.AI;

import com.badlogic.gdx.Gdx;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.objects.entity.Entity;
import com.gensokyouadventure.objects.environment.Torch;

//this state will be entered when the torch walker finishes the path.
//light the target torch and then loop back to looking for torch
public class LightTorch extends AI_State<Entity>
{
	Torch target;
	public LightTorch(AI_FSM<Entity> fsm, Torch target)
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
