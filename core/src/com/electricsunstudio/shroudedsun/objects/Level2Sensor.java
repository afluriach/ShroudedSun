package com.electricsunstudio.shroudedsun.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.environment.Barrier;
import com.electricsunstudio.shroudedsun.objects.Level2Spirit;


public class Level2Sensor extends GameObject
{
	final Rectangle area;
	Barrier barrier;

	int occupancy = 0;
	boolean player_present = false;

	//ideally will only put one in each. but if the player stands in the door
	//with a second one within reach then it is possible to put two in.
	//if flag is set, render something in the main room for this and 
	void extraResult()
	{
		Game.log(getName() + " extra");
	}

	public Level2Sensor(TilespaceRectMapObject to)
	{
		super(to);
		physicsBody = physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody, 1, true, "trap");
		area = to.rect;
	}

	@Override
	public void update()
	{
		if(occupancy > 0 && !player_present && !barrier.isLocked() && !barrier.isBlocked())
		{
			barrier.lock();

			if(occupancy > 1)
			{
				extraResult();
			}
		}
	}

	@Override
	public void render(SpriteBatch sb) {
	}

	@Override
	public void handleContact(GameObject other)
	{
		if(other instanceof Player)
		{
			player_present = true;
			Game.log("player enetered " + name);
		}
		if(other instanceof Level2Spirit)
		{
			++occupancy;
			Game.log(other.name + " enetered " + name);
		}    
	}

	@Override
	public void handleEndContact(GameObject other)
	{
		if(other instanceof Player)
		{
			player_present = false;
		}
		if(other instanceof Level2Spirit)
		{
			--occupancy;
		}
	}

	@Override
	public void onExpire()
	{
		Game.inst.physics.removeBody(physicsBody);
	}

	@Override
	public void init() {

		int sensor_id = Integer.parseInt(name.split("sensor")[1]);

		barrier = (Barrier) Game.inst.gameObjectSystem.getObjectByName("bar"+sensor_id);
	}

}
