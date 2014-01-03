package com.pezventure.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.graphics.Graphics;
import com.pezventure.map.TilespaceRectMapObject;

//barrier is a static collider when locked. change to a Dynamic sensor
//when unlocked

public class Barrier extends GameObject
{
	private Texture texture;
	private boolean isLocked = true;
	
	ArrayList<Switch> switches;
	String[] switchNames;
	String[]  targetNames;
	
	public Barrier(TilespaceRectMapObject to)
	{
		super(to);
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody, "environmental_floor");
		texture = Game.inst.spriteLoader.getTexture("barrier");
		
		if(to.prop.containsKey("switch"))
		{
			switchNames = to.prop.get("switch", String.class).split("\\s+");
		}
		
		if(to.prop.containsKey("cleared"))
		{
			targetNames = to.prop.get("cleared", String.class).split("\\s+");
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
		if(targetNames != null)
		{
			isLocked = !GameObject.allExpired(targetNames);
			setSensor(!isLocked);
		}
	}

	@Override
	public void handleContact(GameObject other)
	{
	}

	@Override
	public void onExpire() {
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
	}

	@Override
	public void init() {
		//init switches here. 
		if(switches == null && switchNames != null)
		{
			switches = new ArrayList<Switch>(switchNames.length);
			
			for(int i=0;i<switchNames.length; ++i)
			{
				switches.add((Switch) Game.inst.gameObjectSystem.getObjectByName(switchNames[i]));
			}
		}

		
	}

	
}
