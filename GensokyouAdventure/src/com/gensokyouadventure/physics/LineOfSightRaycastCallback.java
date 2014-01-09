package com.gensokyouadventure.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.RadarSensor;
import com.gensokyouadventure.objects.PlayerShieldObject;
import com.gensokyouadventure.objects.RenderLayer;

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
		
		if(obj instanceof PlayerShieldObject || obj instanceof RadarSensor || obj.getRenderLayer() == RenderLayer.floor)
		{
			//ignore sensors and other invisible objects that do not obstruct LOS
			//ignore floor objects that do not obstructo LOS
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
