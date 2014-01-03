package com.pezventure.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.graphics.Graphics;
import com.pezventure.map.MapLink;
import com.pezventure.map.TilespaceRectMapObject;

//IDEA: door is a static collider when locked. change to a Dynamic sensor
//when unlocked, this will make transitions between rooms easier to detect

public class Door extends GameObject implements Switch
{
	private Texture lockedTexture;
	private Texture unlockedTexture;
	private boolean isLocked = true;
	
	ArrayList<Switch> switches;
	String[] switchNames;
	
	//a door may operate like a maplink. if destlink is not set, then opening the door
	//will simply involve moving through it to the other side.
	String destMap;
	String destLink;
	
	public Door(TilespaceRectMapObject to)
	{
		super(to);
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody, "environmental_floor");
		lockedTexture = Game.inst.spriteLoader.getTexture("barred_door");
		unlockedTexture = Game.inst.spriteLoader.getTexture("door");
		
		if(to.prop.containsKey("switch"))
			switchNames = to.prop.get("switch", String.class).split("\\s+");
		
		if(to.prop.containsKey("dest_map"))
			destMap = to.prop.get("dest_map", String.class);
		
		if(to.prop.containsKey("dest_link"))
			destLink = to.prop.get("dest_link", String.class);
		
		if(to.prop.containsKey("open"))
			isLocked = false;

	}
	
	public boolean isLocked()
	{
		return isLocked;
	}
	
	//for the switch interface, an unlocked door will mean activated;
	public boolean isActivated()
	{
		return !isLocked;
	}


	@Override
	public void render(SpriteBatch sb)
	{
		Graphics.drawTexture(isLocked ? lockedTexture : unlockedTexture, getCenterPos(), sb);
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
				switches.add( (Switch) Game.inst.gameObjectSystem.getObjectByName(switchNames[i]));
			}
		}
	}
}
