package com.pezventure.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.pezventure.objects.GameObject;

public class LineOfSightRaycastCallback implements RayCastCallback
{
	GameObject target;
	/**
	 * raycast hit the target gameObject. assuming the raycast was aimed correctly, this should happen if
	 * the raycast was actually run and not interrupted.
	 */
	boolean hitTarget = false;
	
	/**
	 * 
	 * @return true if the raycast was run and it hit the target. false if it hit someting else, or if the
	 * raycast has not yet run.
	 */
	public boolean hitTarget()
	{
		return hitTarget;
	}
	
	public LineOfSightRaycastCallback(GameObject target)
	{
		this.target = target;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
	{
		GameObject obj = (GameObject) fixture.getBody().getUserData();
		if(obj != target)
		{
			//raycast hit something else
			//continue to search the first fraction of the ray
			return fraction;
		}
		else
		{
			hitTarget = true;
			return 0;
		}
	}

}
