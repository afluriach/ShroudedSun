package com.gensokyouadventure.objects.enemies;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.AI.AI_FSM;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.Entity;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.Player;

public class Enemy extends Entity
{
	//health and damage
	private static final float invulerabilityLength = 0.5f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	int hp;
	float invulnerableTimeRemaining = 0;
	int touchDamage = 0;

	//ai
	AI_FSM fsm;
	
	public Enemy(TilespaceRectMapObject mo, String animation, int hp)
	{
		super(mo, animation, "enemy", false);
		this.hp = hp;
	}
	
	public void hit(int damage)
	{
		if(invulnerableTimeRemaining == 0)
		{
			hp -= damage;
			invulnerableTimeRemaining = invulerabilityLength;
			enableFlicker(invulerabilityLength, invulerabilityFlickerInterval);
		}
	}
	
	@Override
	public void update()
	{
		if(hp <= 0) expire();
		
		invulnerableTimeRemaining -= Game.SECONDS_PER_FRAME;
		if(invulnerableTimeRemaining < 0)
		{
			invulnerableTimeRemaining = 0;
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    

		if(fsm != null)
			fsm.update();
		super.update();
	}	
	
	@Override
	public void init()
	{
		if(fsm != null)
			fsm.init();
		
	}
	@Override
	public void handleContact(GameObject other)
	{
		if(other instanceof Player && touchDamage > 0)
		{
			((Player)other).hit(touchDamage);
		}

	}
	
	@Override
	public void handleEndContact(GameObject other)
	{
	}


}
