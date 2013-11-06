package com.pezventure.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.physics.Physics;
import com.pezventure.physics.PrimaryDirection;

public class Player extends Entity
{
	private static final float SPEED = 2.0f;
	private static final float PLAYER_SIZE = 0.75f;
	private static final int MAX_HP = 10;
	private static final float invulerabilityLength = 1.0f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	
	int hp = MAX_HP;
	float invulnerableTimeRemaining = 0f;
	
	public Player(Vector2 pos)
	{
		super(pos,
			  PLAYER_SIZE,
			  PLAYER_SIZE,
			  SPEED,
			  Game.inst.spriteLoader.getSpriteAnimation("link_dark_hat", PrimaryDirection.up), "player");
		
		//physicsBody = Physics.inst().addRectBody(pos, PLAYER_SIZE, PLAYER_SIZE, BodyType.DynamicBody, this, MASS, false);
	}
	
	public void update()
	{
		super.update();
		
		invulnerableTimeRemaining -= Game.SECONDS_PER_FRAME;
		if(invulnerableTimeRemaining < 0)
		{
			invulnerableTimeRemaining = 0;
		}
	}
	
	@Override
	public void handleCollision(GameObject other)
	{
		
	}

	@Override
	void onExpire()
	{
		// TODO Auto-generated method stub
		
	}

	public int getHP() {
		return hp;
	}

	public int getMaxHP() {
		return MAX_HP;
	}

	public void heal(int hp)
	{
		this.hp += hp;
		if(this.hp > MAX_HP)
			this.hp = MAX_HP;
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
	
	/**
	 * ignore invulerability effect
	 * @param hp
	 */
	public void directDamage(int hp) 
	{
		this.hp -= hp;
		
	}
}
