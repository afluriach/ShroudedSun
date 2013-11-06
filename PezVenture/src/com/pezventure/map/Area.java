package com.pezventure.map;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.objects.GameObjectSystem;
import com.pezventure.physics.PrimaryDirection;

public abstract class Area
{
	private static final int BLOCK_SIZE = 4;
	
	ArrayList<Room> rooms = new ArrayList<Room>();
	ArrayList<MapLink> links = new ArrayList<MapLink>();
	ArrayList<TilespaceRectMapObject> mapObjects = new ArrayList<TilespaceRectMapObject>();
	
	public abstract void init();
	public abstract void update();
	public abstract void exit();
	
	public Vector2 playerStartPos;
	
	public TiledMap map;
	
	public Area(TiledMap map)
	{
		this.map = map;
		int mapHeightPixels = map.getProperties().get("height", Integer.class)*Game.PIXELS_PER_TILE;
		
		for(MapLayer group : map.getLayers())
		{
			for(MapObject to : group.getObjects())
			{
				MapProperties props = to.getProperties();
				String type = props.get("type", String.class);
				
//				Gdx.app.log(Game.TAG, "to loaded: " + to.getName() + " " + type);
				
				//add all layer properties to properties of each object in layer
				props.putAll(group.getProperties());
				
				if(type != null && type.equals("room"))
				{
					//add as room
					Room r = new Room();
					r.location = MapUtil.tilespaceRect((RectangleMapObject) to, mapHeightPixels);
					r.name = to.getName();
					rooms.add(r);
				}
				else if(type != null && type.equals("map_link"))
				{
					//add as MapLink
					MapLink link = new MapLink();
					link.location = MapUtil.tilespaceRect((RectangleMapObject) to, mapHeightPixels);
					link.entranceDir = PrimaryDirection.valueOf(props.get("entrance_dir", String.class));
				}
				else if(type != null && type.equals("player_start"))
				{
					playerStartPos = MapUtil.tilespacePos(to);
				}
				else
				{
					mapObjects.add(new TilespaceRectMapObject((RectangleMapObject) to, mapHeightPixels));
				}
			}
		}		
	}
	
	/**
	 * instantiates a game object from each map object that represents a game object
	 */
	public void instantiateMapObjects()
	{
		for(TilespaceRectMapObject mo : mapObjects)
		{
			Game.inst.gameObjectSystem.addObject(MapUtil.instantiate(mo));
		}
	}

	public TilespaceRectMapObject getMapObject(String name)
	{
		for(TilespaceRectMapObject mo : mapObjects)
		{
			if(mo.name.equals(name))
			{
				return mo;
			}
		}
		return null;
	}
}
