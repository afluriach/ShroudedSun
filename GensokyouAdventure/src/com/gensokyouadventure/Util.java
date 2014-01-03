package com.gensokyouadventure;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.gensokyouadventure.objects.ClearListener;
import com.gensokyouadventure.objects.FloorSwitch;
import com.gensokyouadventure.objects.Switch;
import com.gensokyouadventure.objects.SwitchListener;
import com.gensokyouadventure.physics.PrimaryDirection;

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
			return "./bin/" + path;
		}
		else
		{
			return path;
		}

	}
	
	public static FileHandle getInternalDirectory(String path)
	{
		return Gdx.files.internal(getInternalPath(path));
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
	
	public static boolean allActivated(Iterable<Switch> iter)
	{
		for(Switch sw : iter)
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
	
	//one-liner to generate a filter with values filled in. would be helpful to create possible filters 
	//in a map and chose one.
	//
	//enemy: collides with 
	//
	//collateral vs non-collateral. enemy attack that may damage other enemies vs not.
	//switches? switches that can only be activated by player or specfic kind of item. could 
	//use filter to prevent anything other than player projectile (say) from colliding.
	
	public static Filter makeFilter(short category, short mask, short group)
	{
		Filter filter = new Filter();
		
		filter.categoryBits = category;
		filter.maskBits = mask;
		filter.groupIndex = group;
		
		return filter;
	}
	
	public static Vector2 get8DirUnit(int dir)
	{
		if(dir == -1)
			return Vector2.Zero;
		
		float angle = dir*45f;
		
		return getUnit(angle);
	}
	
	public static Vector2 getUnit(float angle)
	{
		return new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
	}
	
	public static int getNearestDir(float angle)
	{
		float midpointAngle = angle+22.5f;
		if(midpointAngle > 360f) midpointAngle -= 360f;
		else if(midpointAngle < 0f) midpointAngle += 360f;
			
		int dir = (int) (midpointAngle/45f);
		
//		System.out.println(String.format("angle: %f, dir: %d", angle, dir));
		return dir;
	}
	
	public static boolean switchClearActivation(SwitchListener switchListener, ClearListener clearListener)
	{
		return     (switchListener == null || switchListener.isActivated()) &&
				   (clearListener == null || clearListener.isActivated());
	}
}
