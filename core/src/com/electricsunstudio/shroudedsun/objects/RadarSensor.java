package com.electricsunstudio.shroudedsun.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.Util;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class RadarSensor extends GameObject
{
	public final float radius;
	Iterable<Class<?>> sensingClasses;
	private List<GameObject> objectsWithinRadius = new LinkedList<GameObject>();
	
	public RadarSensor(Vector2 pos, float radius, Iterable<Class<?>> sensingClasses, String sensingType)
	{
		super("radarSensor");
		this.radius = radius;
		this.sensingClasses = sensingClasses;
		
		physicsBody = Game.inst.physics.addCircleBody(pos, radius, BodyType.DynamicBody, this, 1f, true, sensingType);
	}
	
	public List<GameObject> getDetectedObjectsWithinRadius()
	{
		return objectsWithinRadius;
	}
	
	/**
	 * get detected objects based on whether the entity can see them
	 * @param angleFacing the angle the sensing entity is facing, in degrees
	 * @param fovEdge the angle from the center of the field of view to each edge. thus
	 * the total width of the FOV is 2*fovEdge
	 * @return list of detected objects
	 */
	public List<GameObject> getDetectedObjects(float angleFacing, float fovEdge)
	{
		float acos = (float) Math.cos(Math.toRadians(fovEdge));
		
		ArrayList<GameObject> detected = new ArrayList<GameObject>();
		for (GameObject obj : objectsWithinRadius)
		{
			Vector2 disp = obj.getCenterPos().sub(getCenterPos()).nor();
			
			if(Util.ray(angleFacing, 1f).dot(disp) >= acos && Game.inst.physics.lineOfSight(getCenterPos(), obj))
			{
				detected.add(obj);
			}
		}
		return detected;
	}

	@Override
	public void update() {
	}

	@Override
	public void render(SpriteBatch sb) {
	}

	@Override
	public void handleContact(GameObject other)
	{
		//add to detected objects if it of the right class
		for(Class<?> cls : sensingClasses)
		{
			if(cls.isInstance(other))
			{
				objectsWithinRadius.add(other);
				break;
			}
		}		
	}

	@Override
	public void handleEndContact(GameObject other)
	{
		for(Class<?> cls : sensingClasses)
		{
			if(cls.isInstance(other))
			{
				objectsWithinRadius.remove(other);
				break;
			}
		}		
	}

	@Override
	public void onExpire() {
		Game.inst.physics.removeBody(physicsBody);
	}

	@Override
	public void init() {
	}

}
