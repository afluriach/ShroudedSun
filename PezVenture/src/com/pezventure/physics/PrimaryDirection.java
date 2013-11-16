package com.pezventure.physics;

import com.badlogic.gdx.math.Vector2;

public enum PrimaryDirection
{
	right, up, left, down;
	
	public Vector2 getUnitVector()
	{
		int x=0, y=0;
		
		if(this == up)
			y = 1;
		else if(this == down)
			y = -1;
		
		if(this == left)
			x = -1;
		else if(this == right)
			x = 1;
		
		return new Vector2(x, y);
		
	}
	
	public int getInt()
	{
		switch(this)
		{
		case right:
			return 0;
		case up:
			return 1;
		case left:
			return 2;
		case down:
			return 3;
		default:
			throw new IllegalArgumentException("invalid direction");
		}
	}
	
	public float getAngle()
	{
		switch(this)
		{
			case right:
				return 0f;
			case up:
				return 90f;
			case left:
				return 180f;
			case down:
				return 270f;
			default:
				throw new Error();
		}
	}
}
