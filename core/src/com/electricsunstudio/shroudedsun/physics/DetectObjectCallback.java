package com.electricsunstudio.shroudedsun.physics;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.RenderLayer;
import com.electricsunstudio.shroudedsun.objects.PlayerShieldObject;
import com.electricsunstudio.shroudedsun.objects.environment.Wall;


/**
 * detect if there is any object on the floor
 * @author ant
 */
public class DetectObjectCallback implements QueryCallback
{
	//has any object been found
	boolean detected = false;
	//do not count this object as a detection.
	GameObject except;
	
	public boolean detected()
	{
		return detected;
	}
	
	public DetectObjectCallback(GameObject except)
	{
		this.except = except;
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		GameObject go = (GameObject)fixture.getBody().getUserData();
		if(!fixture.isSensor() && //ignore collision with sensor fixture
            go.getRenderLayer() != RenderLayer.floor &&
		   go != except && 
		   !(go instanceof PlayerShieldObject) &&
			!(go instanceof Wall))
		{
			detected = true;
			return false; //object found, can terminate query.
		}
		return true;
	}

}
