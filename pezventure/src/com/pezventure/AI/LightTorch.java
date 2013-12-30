package com.pezventure.AI;

import com.badlogic.gdx.Gdx;
import com.pezventure.Game;
import com.pezventure.objects.Entity;
import com.pezventure.objects.Torch;

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
		if(target.isActivated())
		{
			Gdx.app.log(Game.TAG, String.format("Agent %s tried to light torch %s, but it was alread lit.", fsm.agent.getName(), target.getName()));
		}
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
