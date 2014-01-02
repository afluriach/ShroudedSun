package com.pezventure;

import com.pezventure.objects.Player;
import com.pezventure.objects.PlayerShieldObject;

public class Shield extends ToggleAbility
{
	float timeActive = 0;
	PlayerShieldObject shield;
	
	public Shield()
	{
		super(Game.inst.spriteLoader.getTexture("shield32"));
	}
	
	public boolean canActivate()
	{
		return Game.inst.player.getMP() > 0;
	}
	
	public void onActivate()
	{
		//create shield object
		shield = new PlayerShieldObject(Game.inst.player.getCenterPos());
		shield.setActive(true);
		
		Game.inst.gameObjectSystem.addObject(shield);
		Game.inst.gameObjectSystem.handleAdditions();
	}
	
	//shield will use 1 MP per second, round up to nearest second.
	//meaning the player needs at least one mp to activate shield.
	//
	//accumulate time activated. when >= 1 second, deduct one MP and cancel if the player has no more MP left.
	//if accumulated time is not 0, deduct another 
	public boolean updateActive()
	{
		timeActive += Game.SECONDS_PER_FRAME;
		
		if(timeActive >= 1)
		{
			timeActive -= 1;
			Game.inst.player.useMP(1);
		}
		
		//update position of shield object
		shield.setPos(Game.inst.player.getCenterPos());
		
		return Game.inst.player.getMP() > 0;
	}

	@Override
	public void onDeactivate()
	{
		//delete shield object
		shield.setActive(false);
		shield.expire();
	}
}
