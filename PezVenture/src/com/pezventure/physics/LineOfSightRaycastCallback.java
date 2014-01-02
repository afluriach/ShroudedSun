package com.pezventure.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.RadarSensor;
import com.pezventure.objects.PlayerShieldObject;

public class LineOfSightRaycastCallback implements RayCastCallback
{
	GameObject target;
	boolean obstructed = false;
	
	public boolean hitTarget()
	{
		return !obstructed;
	}
	
	public LineOfSightRaycastCallback(GameObject target)
	{
		this.target = target;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
	{
		GameObject obj = (GameObject) fixture.getBody().getUserData();
		
		if(obj instanceof PlayerShieldObject || obj instanceof RadarSensor)
		{
			//ignore sensors and other invisible objects that do not obstruct LOS
			return -1f;
		}
		else if(obj != target)
		{
			obstructed = true;
			return 0f;
		}
		else
		{
			//obj is target
			//search within rest of ray for obstructing objects
			return fraction;
		}
	}

}
