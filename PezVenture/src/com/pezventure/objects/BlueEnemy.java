package com.pezventure.objects;

import com.pezventure.Game;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.PrimaryDirection;

public class BlueEnemy extends Entity
{
	private static final float SPEED = 1.5f;
	private static final int TOUCH_DAMAGE = 1;
	
	public BlueEnemy(TilespaceRectMapObject to) {
		
		super(to, SPEED, Game.inst.spriteLoader.getSpriteAnimation("link_blue_hat",
				         PrimaryDirection.valueOf(to.prop.get("dir", String.class))));
	}

	@Override
	public void handleCollision(GameObject other)
	{
		if(other instanceof Player)
		{
			((Player)other).hit(TOUCH_DAMAGE);
		}

	}

	@Override
	void onExpire() {
		// TODO Auto-generated method stub

	}

}
