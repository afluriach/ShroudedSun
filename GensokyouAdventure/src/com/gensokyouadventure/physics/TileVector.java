package com.gensokyouadventure.physics;

import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;

/**
 * A Vector represented in tilespace. 1 unit represents the width of one tile as well as 1 meter, 
 * i.e. tile space is unit space are interchangeable for now.
 * 
 * @author ant
 */
public class TileVector extends Vector2 {
	//note: tile and unit space is currently interchangeable. 
	
	public TileVector(float x, float y)
	{
		super(x, y);
	}
	
	public PixelVector getPixelVector()
	{
		return new PixelVector(x*Game.PIXELS_PER_TILE, y*Game.PIXELS_PER_TILE);
	}

	private static final long serialVersionUID = 1344803853842438068L;

}
