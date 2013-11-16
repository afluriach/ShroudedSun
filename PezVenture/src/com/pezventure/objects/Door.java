package com.pezventure.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.graphics.Graphics;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.Physics;

//IDEA: door is a static collider when locked. change to a Dynamic sensor
//when unlocked, this will make transitions between rooms easier to detect

public class Door extends GameObject
{
	private Texture texture;
	private boolean isLocked = true;
	
	public Door(TilespaceRectMapObject to)
	{
		super(to);
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody);
		texture = Game.inst.spriteLoader.getTexture("door");
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
		//no-op
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
	void onExpire() {
		//no-op
	}
	
	public void unlock()
	{
		physicsBody.getFixtureList().get(0).setSensor(true);
		isLocked = false;
	}
	
	public void lock()
	{
		physicsBody.getFixtureList().get(0).setSensor(false);
		isLocked = true;

	}

	@Override
	public void handleEndContact(GameObject other) {
		//no-op
	}

	
}
