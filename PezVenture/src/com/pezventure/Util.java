package com.pezventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.objects.FloorSwitch;
import com.pezventure.objects.GameObject;
import com.pezventure.physics.PrimaryDirection;

public class Util
{
	public static FileHandle getInternalFile(String path)
	{
//		if(Gdx.app.getType() == ApplicationType.Desktop)
//		{
//			return Gdx.files.internal("assets/" + path);
//		}
//		else
//		{
			return Gdx.files.internal(path);
//		}
	}
	
	public static String getInternalPath(String path)
	{
		if(Gdx.app.getType() == ApplicationType.Desktop)
		{
			return "assets/" + path;
		}
		else
		{
			return path;
		}

	}
	
	/**
	 * starting from the center, clear a rectangle a given distance from the corrensponding edge
	 * @param rect 
	 * @param dir direction to move, the edge that will be traversed
	 * @param distance distance from the edge
	 * @return
	 */
	public static Vector2 clearRectangle(Rectangle rect, PrimaryDirection dir, float distance)
	{
		Vector2 v = new Vector2();
		Vector2 center = rect.getCenter(new Vector2());
		
		switch(dir)
		{

		case up:
			v.x = center.x;
			v.y = center.y + rect.height/2 + distance;
			break;
		case down:
			v.x = center.x;
			v.y = center.y - rect.height/2 - distance;
			break;
		case right:
			v.x = center.x + rect.width/2 + distance;
			v.y = center.y;
			break;
		case left:
			v.x = center.x - rect.width/2 - distance;
			v.y = center.y;
			break;
		}
		return v;
	}
	
	public static boolean arrayContains(Object [] arr, Object obj)
	{
		for(Object o : arr)
		{
			if(o==obj) return true;
		}
		return false;
	}
	
	public static boolean allActivated(FloorSwitch [] arr)
	{
		for(FloorSwitch sw : arr)
		{
			if(!sw.isActivated()) return false;
		}
		return true;
	}
	
	public static Vector2 ray(float angleDeg, float dist)
	{
		return new Vector2(dist*MathUtils.cosDeg(angleDeg) , dist*MathUtils.sinDeg(angleDeg));
	}
	
	public static Rectangle pixelSpaceRect(Rectangle tileSpaceRect)
	{
		return new Rectangle(tileSpaceRect.x*Game.PIXELS_PER_TILE,
							 tileSpaceRect.y*Game.PIXELS_PER_TILE,
							 tileSpaceRect.width*Game.PIXELS_PER_TILE,
							 tileSpaceRect.height*Game.PIXELS_PER_TILE);
	}
}
