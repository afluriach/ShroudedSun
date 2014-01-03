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
	
	SwitchListener switchListener;
	ClearListener clearListener;
	
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
			switchListener = new SwitchListener(to.prop.get("switch", String.class));
		
		if(to.prop.containsKey("cleared"))
			clearListener = new ClearListener(to.prop.get("cleared", String.class));
		
		if(to.prop.containsKey("dest_map"))
			destMap = to.prop.get("dest_map", String.class);
		
		if(to.prop.containsKey("dest_link"))
			destLink = to.prop.get("dest_link", String.class);
		
		if(to.prop.containsKey("open"))
			isLocked = false;

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
