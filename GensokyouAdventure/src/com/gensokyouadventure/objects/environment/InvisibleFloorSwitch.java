package com.gensokyouadventure.objects.environment;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gensokyouadventure.map.TilespaceRectMapObject;

public class InvisibleFloorSwitch extends FloorSwitch {

	public InvisibleFloorSwitch(TilespaceRectMapObject to) {
		super(to);
	}
	
	@Override
	public void render(SpriteBatch sb)
	{
		
	}

}
