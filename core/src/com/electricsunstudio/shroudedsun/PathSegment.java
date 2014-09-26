package com.electricsunstudio.shroudedsun;

import com.badlogic.gdx.math.Vector2;

public class PathSegment
{
	public Vector2 start, end, disp;
	
	public PathSegment(Vector2 start, Vector2 end)
	{
		this.start = start;
		this.end = end;
		
		disp = end.cpy().sub(start);
	}
	
	/**
	 * move along path as a parametric line.
	 * @param t [0,1]
	 * @return
	 */
	Vector2 getPosAlongPath(float t)
	{
		if(t < 0 || t > 1)
			throw new IllegalArgumentException("invalid value for t: " + t);
		
		Vector2 p = disp.cpy().scl(t);
		
		return start.cpy().add(p);
	}
}
