package com.electricsunstudio.shroudedsun.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.graphics.Graphics;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Enemy;
import com.electricsunstudio.shroudedsun.objects.projectile.EnemyBullet;

public class PlayerShieldObject extends GameObject
{
	boolean active = false;
	Texture texture;
	final float radius = 1f;
	float knockbackForce = 200f;
	
	public PlayerShieldObject(Vector2 pos)
	{
		super("shield");
		
		physicsBody = Game.inst.physics.addCircleBody(pos, radius, BodyType.DynamicBody, this, 1f, true, "player_shield");
		texture = Game.inst.spriteLoader.getTexture("shield64");
	}
	
	public void activate()
	{
		active = true;
	}
	
	public void deactivate()
	{
		active = false;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}

	@Override
	public void update()
	{
	}

	@Override
	public void render(SpriteBatch sb)
	{
		if(active)
			Graphics.drawTexture(texture, getCenterPos(), sb);
		
	}

	@Override
	public void handleContact(GameObject other)
	{
		if(active)
		{
			if(other instanceof EnemyBullet)
				other.expire();
			else if(other instanceof Enemy)
			{
				//push back 
				Vector2 disp = other.getCenterPos().sub(getCenterPos());
				Vector2 impulse = disp.nor().scl(knockbackForce*Game.SECONDS_PER_FRAME);
				
				other.physicsBody.applyLinearImpulse(impulse, other.getCenterPos(), true);
			}
		}
	}

	@Override
	public void handleEndContact(GameObject other) {
	}


	@Override
	public void init() {
	}

}
