package com.pezventure.map;

import java.util.ArrayList;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.pezventure.DisjointNode;
import com.pezventure.Game;
import com.pezventure.objects.GameObjectSystem;
import com.pezventure.objects.Player;
import com.pezventure.objects.Wall;
import com.pezventure.physics.PrimaryDirection;

public abstract class Area
{
	
	/**
	 * The minimum distance from the center of the player to each edge
	 * for the player to go through a maplink.
	 */
	float maplinkActivationMargin = 0.2f;
	
	ArrayList<Room> rooms = new ArrayList<Room>();
	ArrayList<MapLink> links = new ArrayList<MapLink>();
	ArrayList<TilespaceRectMapObject> mapObjects = new ArrayList<TilespaceRectMapObject>();
	
	public abstract void init();
	public abstract void update();
	public abstract void exit();
	
	public abstract void load(JsonValue val);
	public abstract JsonValue save();
	
	public Vector2 playerStartPos;
	
	public TiledMap map;
	
	public Area(TiledMap map)
	{
		this.map = map;
		
		construct1x1Walls();
		
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
					
					link.name = to.getName();
					link.location = MapUtil.tilespaceRect((RectangleMapObject) to, mapHeightPixels);
					
					if(props.containsKey("entrance_dir"))
						link.entranceDir = PrimaryDirection.valueOf(props.get("entrance_dir", String.class));
					
					if(props.containsKey("dest_link"))
						link.destLink = props.get("dest_link", String.class);
					else link.destLink = "";
					
					if(props.containsKey("dest_map"))
						link.destMap = props.get("dest_map", String.class);
					else link.destMap = "";

					//allowing map or link.
					//map only: go to first link in map (for map with only one link)
					//link only: go to link in this map, do not change area / load new map.
					
//					boolean mapEmpty = link.destMap == "";
//					boolean linkEmpty = link.destLink == "";
//					if((mapEmpty || linkEmpty) && !(mapEmpty && linkEmpty))
//					{
//						if(mapEmpty)
//							throw new MapDataException();
//					}
						
					links.add(link);
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
			System.out.println(mo.name);
		}
		Game.inst.gameObjectSystem.handleAdditions();
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
	
	/**
	 * 
	 * @return A MapLink that the player is standing on, or null if none
	 */
	public MapLink checkMaplinkCollision(Player p)
	{
		//The player should be standing completely inside the maplink's
		//rectangle.
		
		for(MapLink link : links)
		{
			float x = p.getCenterPos().x;
			float y = p.getCenterPos().y;
			Rectangle r = link.location;
			
			if(x >= r.x+maplinkActivationMargin &&
			   x <= r.x+r.width - maplinkActivationMargin &&
			   y >= r.y+maplinkActivationMargin &&
			   y <= r.y + r.height - maplinkActivationMargin)
			{
				return link;
			}
		}
		return null;
	}
	
	public MapLink getMapLink(String name)
	{
		for(MapLink link : links)
		{
			if(link.name.equals(name)) return link;
		}
		return null;
	}
	
	//simple version for now. each cell in the wall layer can become a 1x1 wall.
	public void construct1x1Walls()
	{
		TiledMapTileLayer wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");
		int width = wallLayer.getWidth();
		int height = wallLayer.getHeight();

		for(int i=0;i<height; ++i)
		{
			for(int j=0;j<width; ++j)
			{
				if(wallLayer.getCell(j,  i) != null)
				{
					Game.inst.gameObjectSystem.addObject(new Wall(new Rectangle(j, i, 1, 1)));
				}
			}
		}

	}
	
	public void constructWallsFromMap()
	{
		//disjoint set problem. check walls tile layer and find which tiles (cells)
		//have walls. join adjacent cells to make rectangular wall objects. 
		
		TiledMapTileLayer wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");
		int width = wallLayer.getWidth();
		int height = wallLayer.getHeight();
		
		TreeMap<Vector2, DisjointNode> gridNodes = new TreeMap<Vector2, DisjointNode>();
		
////		Gdx.app.log(Game.TAG, String.format("cell at 0,0: %s; cell at 1,1: %s", wallLayer.getCell(0,0) == null ? "null" : "tile0", wallLayer.getCell(1,1) == null ? "null" : "tile1" ));
		
		for(int i=0;i<height; ++i)
		{
			for(int j=0;j<width; ++j)
			{
				if(wallLayer.getCell(j,  i) != null)
				{
					gridNodes.put(new Vector2(j, i), new DisjointNode(j, i));
				}
			}
		}
		
		for(DisjointNode node : gridNodes.values())
		{
			if(node.rect.width == 1)
			{
				//can expand rectangle upward
				
				while(true)
				{
					Vector2 joinPos = new Vector2(node.rect.x, node.rect.y + node.rect.height);
					if(gridNodes.containsKey(joinPos))
					{
						DisjointNode other = gridNodes.get(joinPos);
						if(other.rect.width ==1)
						{
							
						}
					}
					else break;
				}
				
				
			}
			else if(node.rect.height ==1)
			{
				//can expand the rectangle rightwards
			}
		}
	}
	
	public Room getCurrentRoom(Vector2 pos)
	{
		for(Room r : rooms)
		{
			if(r.location.contains(pos)) return r;
		}
		return null;
	}
}
