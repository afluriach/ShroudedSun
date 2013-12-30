package com.pezventure.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pezventure.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class RadarSensor extends GameObject
{
	public final float radius;
	Class<?> sensingClass;
	private List<GameObject> detectedObjects = new LinkedList<GameObject>();
	//objects that are in the contact with the radar, but are not visible due to line of sight.
	private List<GameObject> obstructedObjects = new LinkedList<GameObject>();
	
	public RadarSensor(Vector2 pos, float radius, Class<?> sensingClass, String sensingType)
	{
		super("radarSensor");
		this.radius = radius;
		this.sensingClass = sensingClass;
		
		physicsBody = Game.inst.physics.addCircleBody(pos, radius, BodyType.DynamicBody, this, 1f, true, sensingType);
	}
	
	public List<GameObject> getDetectedObjects()
	{
		return detectedObjects;
	}
	
	/**
	 * get detected objects based on whether the entity can see them
	 * @param angleFacing the angle the sensing entity is facing, in degrees
	 * @param fovEdge the angle from the center of the field of view to each edge. thus
	 * the total width of the FOV is 2*fovEdge
	 * @return list of detected objects
	 */
	public List<GameObject> getDetectedOjects(float angleFacing, float fovEdge)
	{
		ArrayList<GameObject> detected = new ArrayList<GameObject>();
		for (GameObject obj : detectedObjects)
		{

			Vector2 disp = obj.getCenterPos().sub(getCenterPos());
			//Gdx.app.log(Game.TAG, String.format("facing: %f, target dir: %f", angleFacing, disp.angle()));			
			if(Math.abs(disp.angle() - angleFacing) <= fovEdge)
			{
				detected.add(obj);
			}
		}
		return detected;
	}

	@Override
	public void update() {
		
		//check to see if any previously occluded object within radius of the sensor has since
		//entered line of sight
		Iterator<GameObject> obstructedIter = obstructedObjects.iterator();
		
		while(obstructedIter.hasNext())
		{
			GameObject go = obstructedIter.next();
			
			if(Game.inst.physics.lineOfSight(getCenterPos(), go))
			{
				detectedObjects.add(go);
				obstructedIter.remove();
			}
		}
		
		//convsersely, check to see if any detected object within radius has left line of sight
		
		Iterator<GameObject> detectedIter = detectedObjects.iterator();
		
		while(detectedIter.hasNext())
		{
			GameObject go = detectedIter.next();
			
			if(!Game.inst.physics.lineOfSight(getCenterPos(), go))
			{
				obstructedObjects.remove(go);
				detectedIter.remove();
			}
		}
	}

	@Override
	public void render(SpriteBatch sb) {
	}

	@Override
	public void handleContact(GameObject other)
	{
		//add to detected objects if it of the right class and there is a line of sight.
		//assuming enemy is at the center of the radar.
		if(sensingClass.isInstance(other))
		{
			if(Game.inst.physics.lineOfSight(getCenterPos(), other))
			{
				detectedObjects.add(other);
			}
			else
			{
				obstructedObjects.add(other);
			}
		}
	}

	@Override
	public void handleEndContact(GameObject other)
	{
		if(sensingClass.isInstance(other))
		{
			detectedObjects.remove(other);
			obstructedObjects.remove(other);
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
