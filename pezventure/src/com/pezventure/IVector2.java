package com.pezventure;

import com.badlogic.gdx.math.Vector2;

/**
 * 2D integer vector
 * @author ant
 *
 */
public class IVector2 
{
	public int x, y;
	
	public IVector2()
	{
		x = y = 0;
	}
	
	public IVector2(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public IVector2(Vector2 v)
	{
		x = (int) v.x;
		y = (int) v.y;
	}
	
	public IVector2(IVector2 v)
	{
		x = v.x;
		y = v.y;
	}
	
	public IVector2 copy()
	{
		return new IVector2(this);
	}
	
	public boolean equals(IVector2 o)
	{
		return x == o.x && y == o.y;
	}
	
	public Vector2 getFloat()
	{
		return new Vector2(x,y);
	}
}
