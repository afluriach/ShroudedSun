package com.gensokyouadventure.map;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;

public class Path
{
	public ArrayList<Vector2> points;
	
	boolean loop;
	
	public Path(float [] verts, boolean loop, Vector2 origin)
	{
		this.loop = loop;
		
		if(verts.length % 2 != 0)
			throw new MapDataException("verticies array cannot be odd");
		if(verts.length < 4)
			throw new MapDataException("path must have at least two points");
		
		int len = verts.length / 2;
		points = new ArrayList<Vector2>(len);
		
		for(int i=0;i<len; ++i)
		{
			Vector2 tilespacePoint = new Vector2(verts[2*i], verts[2*i+1]).add(origin);
			tilespacePoint.x *= Game.TILES_PER_PIXEL;
			tilespacePoint.y *= Game.TILES_PER_PIXEL;
			
			points.add(tilespacePoint);
		}
		
	}
	
	public Vector2 getPoint(int index)
	{
		return points.get(index);
	}
	
	public int length()
	{
		return points.size();
	}
	
	public boolean isLoop()
	{
		return loop;
	}
}
