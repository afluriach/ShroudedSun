package com.electricsunstudio.shroudedsun.objects.entity.enemies;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.AI.AI_FSM;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.Entity;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;
import com.electricsunstudio.shroudedsun.objects.projectile.EnemyBullet;

public abstract class Enemy extends Entity
{
	//health and damage
	private static final float invulerabilityLength = 0.5f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	int hp;
	float invulnerableTimeRemaining = 0;
	int touchDamage = 0;
	boolean playerTouching;
	protected boolean canDamage = true;
	
	public Enemy(TilespaceRectMapObject mo, String animation, int hp)
	{
		super(mo, animation, "enemy", BodyType.DynamicBody);
		this.hp = hp;
	}
	
	public void canDamage(boolean canDamage)
	{
		this.canDamage = canDamage;
	}
	
	public void hit(int damage)
	{
		if(invulnerableTimeRemaining == 0 && canDamage)
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
		
		if(playerTouching)
		{
			Game.inst.player.hit(touchDamage);
		}
		
		invulnerableTimeRemaining -= Game.SECONDS_PER_FRAME;
		if(invulnerableTimeRemaining < 0)
		{
			invulnerableTimeRemaining = 0;
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    

		super.update();
	}	
	
	@Override
	public void handleContact(GameObject other)
	{
		//track if player is remaining in contact with enemy.
		//since this only gets called once per contact.
		if(other instanceof Player && touchDamage > 0)
		{
			((Player)other).hit(touchDamage);
			playerTouching = true;
		}

	}
	
	@Override
	public void handleEndContact(GameObject other)
	{
		if(other instanceof Player)
		{
			playerTouching = false;
		}
	}
	
	public abstract EnemyBullet getBullet();
}
