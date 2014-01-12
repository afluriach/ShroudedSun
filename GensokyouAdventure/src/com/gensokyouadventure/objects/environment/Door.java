package com.gensokyouadventure.objects.environment;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.map.MapLink;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.ClearListener;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.SwitchListener;

//IDEA: door is a static collider when locked. change to a Dynamic sensor
//when unlocked, this will make transitions between rooms easier to detect

public class Door extends GameObject implements Switch
{
	private Texture lockedTexture;
	private Texture unlockedTexture;
	private boolean isLocked = true;
	//a double door is paired with another door immediately next to it. the player will move two units
	//when traversing through this door to get through the other door
	public boolean doubleDoor;
	
	SwitchListener switchListener;
	ClearListener clearListener;
	
	//a door may operate like a maplink. if destlink is not set, then opening the door
	//will simply involve moving through it to the other side.
	String destMap;
	String destLink;
	
	public String getDestLink() {
		return destLink;
	}
	public String getDestMap() {
		return destMap;
	}


	public Door(TilespaceRectMapObject to)
	{
		super(to);
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody, "environmental_floor");
		lockedTexture = Game.inst.spriteLoader.getTexture("barred_door");
		unlockedTexture = Game.inst.spriteLoader.getTexture("door");
		
		if(to.prop.containsKey("switch"))
			switchListener = new SwitchListener(to.prop.get("switch", String.class));
		
		if(to.prop.containsKey("cleared"))
			clearListener = new ClearListener(to.prop.get("cleared", String.class));
		
		if(to.prop.containsKey("dest_map"))
			destMap = to.prop.get("dest_map", String.class);
		
		if(to.prop.containsKey("dest_link"))
			destLink = to.prop.get("dest_link", String.class);
		
		if(to.prop.containsKey("open"))
			isLocked = false;
		
		doubleDoor = (to.prop.containsKey("double"));

		//door is a solid object, unlike a barrier which disappears when unlocked.
		setSensor(false);
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
		//if one listener or both are not null, they will be checked for door activation.
		//if both are not null, both need to be activated to unlock the door.
		//
		//do not unlock door if no listener. in this case, the door is probably unlocked in an
		//event-based manner somewhere else.
		
		if(switchListener != null) switchListener.update();
		if(clearListener != null) clearListener.update();
		
		if(switchListener != null || clearListener != null)
		{
			isLocked = !Util.switchClearActivation(switchListener, clearListener);
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
		isLocked = false;
	}
	
	public void lock()
	{
		isLocked = true;
	}

	@Override
	public void handleEndContact(GameObject other) {
	}

	@Override
	public void init()
	{
		if(switchListener != null) switchListener.init();
	}
}
