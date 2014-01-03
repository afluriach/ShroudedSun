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

public class Barrier extends GameObject implements Switch
{
	private Texture texture;
	private boolean isLocked = true;
	
	SwitchListener switchListener;
	ClearListener clearListener;
	
	public Barrier(TilespaceRectMapObject to)
	{
		super(to);
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody, "environmental_floor");
		texture = Game.inst.spriteLoader.getTexture("barrier");
		
		if(to.prop.containsKey("switch"))
		{
			switchListener = new SwitchListener(to.prop.get("switch", String.class));
		}
		
		if(to.prop.containsKey("cleared"))
		{
			clearListener = new ClearListener(to.prop.get("cleared", String.class));
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
		
		setSensor(!isLocked);
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
	public void init()
	{
		if(switchListener != null) switchListener.init();
	}
	@Override
	public boolean isActivated()
	{
		return !isLocked;
	}

	
}
