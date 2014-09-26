package com.electricsunstudio.shroudedsun.map;

import java.util.ArrayList;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.Game;

public class Path
{
	public ArrayList<Vector2> points;
	
	boolean loop;
	
	public Path(TilespacePolylineMapObject mo)
	{
		loop = mo.prop.containsKey("loop") && mo.prop.get("loop", String.class).equals("true");
		points = mo.points;
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
