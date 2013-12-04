package com.pezventure.physics;

import java.util.LinkedList;

import com.badlogic.gdx.math.Vector2;
import com.pezventure.objects.GameObject;

public class RaycastResult
{
	//going from 0 to 1 representing a fraction along the Vector
	private float unitDist;
	private Vector2 ray;
	private LinkedList<GameObject> detectedObjects;
	
	public float getDist()
	{
		return ray.len()*unitDist;
	}
	
	public float getUnitDist()
	{
		return unitDist;
	}
}
