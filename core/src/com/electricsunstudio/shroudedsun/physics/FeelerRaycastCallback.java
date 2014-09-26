package com.electricsunstudio.shroudedsun.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.electricsunstudio.shroudedsun.objects.GameObject;

/**
 * Handles raycasts that feel for a class of gameobject
 * @author ant
 *
 */
public class FeelerRaycastCallback implements RayCastCallback
{
	Class<?> targetCls;
	GameObject target;
	//position along the ray (as given by fraction). if the ray intersects multiple targets,
	//make sure the closest one is reported
	float targetPos;
	
	
	/**
	 * 
	 * @param targetCls class of object to search for. 
	 */
	public FeelerRaycastCallback(Class<?> targetCls)
	{
		this.targetCls = targetCls;
	}
	
	public GameObject getResult()
	{
		return target;
	}
	
	public float getFeelerDistFraction()
	{
		return targetPos;
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction)
	{
		GameObject go = (GameObject) fixture.getBody().getUserData();
		if(targetCls.isInstance(go))
		{
			//if this is the first or the closest
			if(target == null || fraction < targetPos)
			{
				target = go;
				targetPos = fraction;
			}
		}
		//note: function does not necessarily get called based on the order of the fixtures along the
		//ray. if the ray hits an object of the wrong type, a valid target object may be less than fraction
		//along the ray
		//
		//similarly, if the ray hits an object of the right type, there may be another valid target in front of the
		//object
		return fraction;
	}
	
	

}
