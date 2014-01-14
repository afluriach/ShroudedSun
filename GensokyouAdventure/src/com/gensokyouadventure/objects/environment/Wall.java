package com.gensokyouadventure.objects.environment;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.GameObject;

public class Wall extends GameObject
{
	public Wall(TilespaceRectMapObject to)
	{
		super(to);
		
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody, 1, false, "wall");
	}
	
	public Wall(Rectangle r)
	{
		super("wall");
		physicsBody = Game.inst.physics.addRectBody(r, this, BodyType.StaticBody, 1, false, "wall");

	}

	@Override
	public void render(SpriteBatch sb)
	{
		//no-op. walls are invisible physics objects, already represented
		//visually in the tile map
	}

	@Override
	public void update()
	{
	}

	@Override
	public void handleContact(GameObject other)
	{
	}

	@Override
	public void onExpire()
	{
	}
	
	@Override
	public void handleEndContact(GameObject other)
	{
	}

	@Override
	public void init()
	{
	}


}