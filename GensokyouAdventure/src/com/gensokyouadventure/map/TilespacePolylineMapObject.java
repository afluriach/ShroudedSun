package com.gensokyouadventure.map;

import java.util.ArrayList;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;

public class TilespacePolylineMapObject
{
	public ArrayList<Vector2> points;
	public MapProperties prop;
	public String name;
	public String type;
	
	public TilespacePolylineMapObject(PolylineMapObject mo)
	{
		name = mo.getName();
		type = mo.getProperties().get("type", String.class);		
		this.prop = mo.getProperties();
		
		Vector2 origin = new Vector2();
		origin.x = mo.getProperties().get("x", Float.class);
		origin.y = mo.getProperties().get("y", Float.class);
		
		calculatePoints(mo.getPolyline().getVertices(), origin);
	}
	
	private void calculatePoints(float [] verts, Vector2 origin)
	{
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
}
