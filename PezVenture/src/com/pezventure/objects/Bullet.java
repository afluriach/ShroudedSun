package com.pezventure.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.physics.PrimaryDirection;

public abstract class Bullet extends GameObject
{
	PrimaryDirection dir;
	float radius;
	
	public Bullet(Vector2 pos, float radius, String name, PrimaryDirection dir, float speed, float mass)
	{
		super(name);
		physicsBody = Game.inst.physics.addCircleBody(pos, radius, BodyType.DynamicBody, this, mass, false);
		setVel(dir.getUnitVector().scl(speed));
		this.dir = dir;
		this.radius = radius;
	}
	
	public PrimaryDirection getDir()
	{
		return dir;
	}
	
	public float getRadius()
	{
		return radius;
	}
	
	@Override
	public void handleContact(GameObject other)
	{
		if(other instanceof Wall || other instanceof Block || other instanceof Door)
			expire();
	}
}
