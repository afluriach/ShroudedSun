package com.pezventure.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.graphics.Graphics;
import com.pezventure.map.TilespaceRectMapObject;

//IDEA: door is a static collider when locked. change to a Dynamic sensor
//when unlocked, this will make transitions between rooms easier to detect

public class Door extends GameObject
{
	private Texture texture;
	private boolean isLocked = true;
	
	FloorSwitch[] switches;
	String[] switchNames;
	
	public Door(TilespaceRectMapObject to)
	{
		super(to);
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody, "environmental_floor");
		texture = Game.inst.spriteLoader.getTexture("door");
		
		if(to.prop.containsKey("switch"))
		{
			switchNames = to.prop.get("switch", String.class).split("\\s+");
		}
	}
	
	public boolean isLocked()
	{
		return isLocked;
	}

	@Override
	public void render(SpriteBatch sb)
	{
		if(isLocked)
		{
			Graphics.drawTexture(texture, getCenterPos(), sb);
		}
	}

	@Override
	public void update()
	{		
		if(switches != null)
		{
			isLocked = !Util.allActivated(switches);
			setSensor(!isLocked);
		}
	}

	@Override
	public void handleContact(GameObject other)
	{
		if(isLocked)
		{
//			pushCollider(other);
//			other.undoFrameVel();
		}
	}

	@Override
	public void onExpire() {
		//no-op
	}
	
	public void unlock()
	{
		setSensor(true);
		isLocked = false;
	}
	
	public void lock()
	{
		setSensor(false);
		isLocked = true;

	}

	@Override
	public void handleEndContact(GameObject other) {
		//no-op
	}

	@Override
	public void init() {
		//init switches here. 
		if(switches == null && switchNames != null)
		{
			switches = new FloorSwitch[switchNames.length];
			
			for(int i=0;i<switchNames.length; ++i)
			{
				switches[i] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName(switchNames[i]);
			}
		}

		
	}

	
}
