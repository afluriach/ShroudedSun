package com.electricsunstudio.shroudedsun.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.RadarSensor;
import com.electricsunstudio.shroudedsun.objects.RenderLayer;
import com.electricsunstudio.shroudedsun.objects.PlayerShieldObject;
import com.electricsunstudio.shroudedsun.objects.entity.NPC;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Enemy;

/**
 * detect if there is any object at a particular point
 * @author ant
 */
public class FindTargetableObjectAtPointCallback implements QueryCallback
{
	//find closest object. save distance to found object
	float dist2;
	GameObject detected = null;
	//do not count this object as a detection.
	GameObject except;
	Vector2 point;
	
	public GameObject detected()
	{
		return detected;
	}
	
	public FindTargetableObjectAtPointCallback(GameObject except, Vector2 point)
	{
		this.except = except;
		this.point = point;
	}
		
	public boolean reportFixture(Fixture fixture)
	{
		GameObject go = (GameObject)fixture.getBody().getUserData();
		
		if(!(go instanceof Enemy) && !(go instanceof NPC)) return true;
		
		if(fixture.testPoint(point))
		{
			float crntDist2 = go.getCenterPos().sub(point).len2();
			
			if(detected == null)
			{
				detected = go;
				dist2 = crntDist2;				
			}
			else if(crntDist2 < dist2)
			{
				detected = go;
				dist2 = crntDist2;
			}
		}
		return true;
	}

}
