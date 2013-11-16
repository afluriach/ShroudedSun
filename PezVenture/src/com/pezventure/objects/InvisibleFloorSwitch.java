package com.pezventure.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pezventure.map.TilespaceRectMapObject;

public class InvisibleFloorSwitch extends FloorSwitch {

	public InvisibleFloorSwitch(TilespaceRectMapObject to) {
		super(to);
	}
	
	@Override
	public void render(SpriteBatch sb)
	{
		
	}

}
