package com.electricsunstudio.shroudedsun.objects.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.Util;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.PlayerShieldObject;
import com.electricsunstudio.shroudedsun.objects.RadarSensor;
import com.electricsunstudio.shroudedsun.objects.RenderLayer;

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
		renderLayer = RenderLayer.aboveGround;
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
		if(other.getRenderLayer()== RenderLayer.groundLevel &&
		   other.getClass() != RadarSensor.class && 
		   other.getClass() != PlayerShieldObject.class)
			expire();
	}
}
