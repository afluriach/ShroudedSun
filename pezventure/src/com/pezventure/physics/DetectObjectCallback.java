package com.pezventure.physics;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.pezventure.Game;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.RadarSensor;
import com.pezventure.objects.RenderLayer;
import com.pezventure.objects.Shield;

/**
 * detect if there is any object on the floor
 * @author ant
 */
public class DetectObjectCallback implements QueryCallback
{
	//has any object bee found
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
		   !(go instanceof Shield))
		{
			detected = true;
			return false; //object found, can terminate query.
		}
		return true;
	}

}
