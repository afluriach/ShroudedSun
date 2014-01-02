package com.pezventure.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pezventure.Game;
import com.pezventure.Util;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class RadarSensor extends GameObject
{
	public final float radius;
	Class<?> sensingClass;
	private List<GameObject> objectsWithinRadius = new LinkedList<GameObject>();
	//objects that are in the contact with the radar, but are not visible due to line of sight.
	private List<GameObject> obstructedObjects = new LinkedList<GameObject>();
	
	public RadarSensor(Vector2 pos, float radius, Class<?> sensingClass, String sensingType)
	{
		super("radarSensor");
		this.radius = radius;
		this.sensingClass = sensingClass;
		
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
			
//			Gdx.app.log(Game.TAG, String.format("facing: %f, target dir: %f", angleFacing, disp.angle()));			
			
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
		//add to detected objects if it of the right class and there is a line of sight.
		//assuming enemy is at the center of the radar.
		if(sensingClass.isInstance(other))
		{
			objectsWithinRadius.add(other);
		}
	}

	@Override
	public void handleEndContact(GameObject other)
	{
		if(sensingClass.isInstance(other))
		{
			objectsWithinRadius.remove(other);
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
