package com.pezventure.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.Util;

public abstract class Bullet extends GameObject
{
	float radius;
	float angle;
	
	public Bullet(Vector2 pos, float radius, String name, float angle, float speed, float mass, String filter)
	{
		super(name);
		physicsBody = Game.inst.physics.addCircleBody(pos, radius, BodyType.DynamicBody, this, mass, false, filter);
		setVel(Util.ray(angle, speed));
		this.radius = radius;
		this.angle = angle;
	}
		
	public float getRadius()
	{
		return radius;
	}
	
	public float getAngle()
	{
		return angle;
	}
	
	@Override
	public void handleContact(GameObject other)
	{
		if(other.renderLayer== RenderLayer.above_floor &&
		   other.getClass() != RadarSensor.class && 
		   other.getClass() != Shield.class)
			expire();
	}
}
