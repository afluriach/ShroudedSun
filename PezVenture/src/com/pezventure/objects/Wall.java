package com.pezventure.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.Physics;

public class Wall extends GameObject
{
	public Wall(TilespaceRectMapObject to)
	{
		super(to);
		
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody, 1, false);
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
		// no-op
	}

	@Override
	public void handleCollision(GameObject other)
	{
//		if(other instanceof Wall || other instanceof Door)
//			return;
		//pushCollider(other);
//		other.undoFrameVel();
//		other.setVel(Vector2.Zero);
	}

	@Override
	void onExpire()
	{
		//no-op, should not happen
	}

}
