package com.gensokyouadventure.map;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.Wall;
import com.gensokyouadventure.physics.PixelVector;

public class MapUtil
{
	private static TmxMapLoader mapLoader;
	private static Parameters parameters; 
		
	public static TiledMap loadMap(String filename)
	{
		if(mapLoader == null)
		{
			mapLoader = new TmxMapLoader();
			parameters = new Parameters();
			parameters.yUp = true;
		}
		
		return mapLoader.load(filename, parameters);
	}
	
	public static Rectangle toTilespaceRect(Rectangle pix)
	{
		Rectangle tile = new Rectangle();
		
		tile.x = pix.x * Game.TILES_PER_PIXEL;
		tile.y = pix.y * Game.TILES_PER_PIXEL;
		tile.height = pix.height * Game.TILES_PER_PIXEL;
		tile.width = pix.width * Game.TILES_PER_PIXEL;
		
		return tile;
	}
	
	/**
	 * 
	 * Convert y position based on +y pointing up, convert to tilespace units
	 * 
	 * @param to 
	 * @param mapHeightPixels
	 * @return
	 */
	public static Rectangle tilespaceRect(RectangleMapObject to, int mapHeightPixels)
	{
		Rectangle pixRect = ((RectangleMapObject)to).getRectangle();
				
		return toTilespaceRect(pixRect);
	}
	public static Vector2 tilespacePos(MapObject to)
	{
		int x = to.getProperties().get("x", Integer.class);
		int y = to.getProperties().get("y", Integer.class);
		return new Vector2(x*Game.TILES_PER_PIXEL, y*Game.TILES_PER_PIXEL);
	}
	
	public static Area instantiate(Class<?> type)
	{
		Constructor<?> cons = null;
		
		try
		{
			cons = type.getConstructor();
		}
		catch(Exception ex)
		{
			Game.log("Error instantiating type: " + type + " no suitable constructor found");
			throw new RuntimeException("Error instantiating type: " + type + " no suitable constructor found");
		}
		
		try
		{
			return (Area) cons.newInstance();
		}
		catch (Exception e)
		{
			Game.log( "Error instantiating type: " + type + " constructor exception: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new RuntimeException("Error instantiating type: " + type + " constructor exception");
		}

	}
	
	public static GameObject instantiate(TilespaceRectMapObject to)
	{
		if(to.type == null)
			throw new RuntimeException(String.format("Object %s has null type", to.name));
		
		Class<?> type = GameObject.getObjectClass(to.type);
		
		Constructor<?> cons = null;
		
		try
		{
			cons = type.getConstructor(TilespaceRectMapObject.class);
		}
		catch(Exception ex)
		{
			Game.log("Error instantiating type: " + type + " no suitable constructor found");
		}
		
		try
		{
			return (GameObject) cons.newInstance(to);
		}
		catch (Exception e)
		{
			Game.log( String.format("Error instantiating GameObject, class: %s, name: %s", type, to.name));
			e.printStackTrace();
			throw new RuntimeException("constructor exception");
		}
	}
}
