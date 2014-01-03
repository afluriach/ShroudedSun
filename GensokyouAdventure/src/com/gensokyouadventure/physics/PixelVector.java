package com.gensokyouadventure.physics;

import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;

/**
 * Vector in pixel space
 * @author ant
 *
 */
public class PixelVector extends Vector2
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5459106903412102826L;

	public PixelVector(float x, float y)
	{
		super(x, y);
	}

	public TileVector getTileVector()
	{
		return new TileVector(x*Game.TILES_PER_PIXEL, y*Game.TILES_PER_PIXEL);
	}
}
