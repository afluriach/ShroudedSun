package com.gensokyouadventure.map;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.graphics.SpriteLoader;
import com.gensokyouadventure.objects.Entity;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.RandomWalkNPC;
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
	
	//taken from the init method for the NPC meeting room. 
	public static void loadNPCs()
	{
		int entityIdx=0;
		
		for(int i=0;i<6;++i)
		{
			for(int j=0;j<6;++j, ++entityIdx)
			{
				Vector2 pos = new Vector2(4+i*6, 4+j*6);
				String name = Entity.entityNames[entityIdx];
								
				Game.inst.gameObjectSystem.addObject(new RandomWalkNPC(pos, name, 2, name));
			}
		}
	}
	
	public static List<TilespaceRectMapObject> generateNpcMapObjects()
	{
		List<TilespaceRectMapObject> mapObjects = new ArrayList<TilespaceRectMapObject>();
		
		int entityIdx = 0;
		
		for(int i=0;i<6;++i)
		{
			for(int j=0;j<6;++j, ++entityIdx)
			{
				Vector2 pos = new Vector2(4+i*6, 4+j*6);
				Rectangle rect = new Rectangle();
				rect.setCenter(pos);
				rect.setHeight(1);
				rect.setWidth(1);
				
				String name = Entity.entityNames[entityIdx];

				//in this case, the name of the entity is also the name of the sprite to use.
				//need to put this in MapProperties so the NPC constructor can load it.
				MapProperties prop = new MapProperties();
				prop.put("sprite", name);
				
				mapObjects.add(new TilespaceRectMapObject(name, "random_walk_npc", rect, prop));				
			}
		}
		
		return mapObjects;
	}
}
