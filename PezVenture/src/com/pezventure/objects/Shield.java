package com.pezventure.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.graphics.Graphics;

public class Shield extends GameObject
{
	boolean active = false;
	Texture texture;
	final float radius = 1f;
	float knockbackForce = 200f;
	
	public Shield(Vector2 pos)
	{
		super("shield");
		
		physicsBody = Game.inst.physics.addCircleBody(pos, radius, BodyType.DynamicBody, this, 1f, true);
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
		Gdx.app.log(Game.TAG, "shield active: " + active);
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
				
			}
		}
	}

	@Override
	public void handleEndContact(GameObject other) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onExpire() {
		// TODO Auto-generated method stub
		
	}

}