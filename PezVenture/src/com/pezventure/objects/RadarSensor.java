package com.pezventure.objects;

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
	
	public RadarSensor(Vector2 pos, float radius, Class<?> sensingClass)
	{
		super("radarSensor");
		this.radius = radius;
		this.sensingClass = sensingClass;
		
		physicsBody = Game.inst.physics.addCircleBody(pos, radius, BodyType.DynamicBody, this, 1f, true);
	}
	
	public List<GameObject> getDetectedObjects()
	{
		return detectedObjects;
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
		if(sensingClass.isInstance(other) && Game.inst.physics.lineOfSight(getCenterPos(), other))
		{
			detectedObjects.add(other);
		}
	}

	@Override
	public void handleEndContact(GameObject other)
	{
		if(sensingClass.isInstance(other))
		{
			detectedObjects.remove(other);
		}
	}

	@Override
	void onExpire() {
	}

}
