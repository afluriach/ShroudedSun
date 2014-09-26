package com.electricsunstudio.shroudedsun.physics;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.RadarSensor;
import com.electricsunstudio.shroudedsun.objects.RenderLayer;
import com.electricsunstudio.shroudedsun.objects.PlayerShieldObject;

/**
 * detect if there is any object on the floor
 * @author ant
 */
public class DetectObjectCallback implements QueryCallback
{
	//has any object been found
	boolean detected = false;
	//do not count this object as a detection..
	GameObject except;
	
	public boolean detected()
	{
		return detected;
	}
	
	public DetectObjectCallback(GameObject except)
	{
		this.except = except;
	}
		
	public boolean reportFixture(Fixture fixture)
	{
		GameObject go = (GameObject)fixture.getBody().getUserData();
		if(go.getRenderLayer() != RenderLayer.floor &&
		   go != except && 
		   !(go instanceof RadarSensor) &&
		   !(go instanceof PlayerShieldObject))
		{
			detected = true;
			return false; //object found, can terminate query.
		}
		return true;
	}

}
