package com.pezventure.objects;

import java.util.ArrayList;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.map.TilespaceRectMapObject;


public abstract class NPC extends Entity
{	
	public NPC(TilespaceRectMapObject to)
	{
		super(to, to.prop.get("sprite", String.class), "npc", to.type.equals("stationary_npc"));

		//speed is a propety of entity and the speed key is already checked.
		//class specific default in case speed is not specified
		if(!to.prop.containsKey("speed"))
		{
			speed = defaultSpeed;
		}

	}
	
	public NPC(Vector2 pos, String name, int startingDir, String animation, boolean stationary)
	{
		super(pos, name, startingDir, animation, "npc", stationary);
		speed = defaultSpeed;
	}
	
	public abstract void talk();	
}
