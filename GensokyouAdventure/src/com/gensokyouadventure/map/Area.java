package com.gensokyouadventure.map;

import java.util.ArrayList;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.gensokyouadventure.DisjointNode;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.circuit.CircuitGraph;
import com.gensokyouadventure.objects.circuit.LogicGateTorch;
import com.gensokyouadventure.objects.circuit.Wire;
import com.gensokyouadventure.objects.entity.characters.Player;
import com.gensokyouadventure.objects.environment.Door;
import com.gensokyouadventure.objects.environment.Switch;
import com.gensokyouadventure.objects.environment.Wall;
import com.gensokyouadventure.physics.PrimaryDirection;

public class Area
{
	
	/**
	 * The minimum distance from the center of the player to each edge
	 * for the player to go through a maplink.
	 */
	float maplinkActivationMargin = 0.2f;
	
	TreeMap<String, Room> rooms = new TreeMap<String, Room>();
	ArrayList<MapLink> links = new ArrayList<MapLink>();
	ArrayList<TilespaceRectMapObject> rectMapObjects = new ArrayList<TilespaceRectMapObject>();
	ArrayList<GameObject> circuitObjects = new ArrayList<GameObject>();
	TreeMap<String, Path> paths = new TreeMap<String, Path>();
	public String musicTitle;
	public CircuitGraph circuitGraph;
		
	public AreaState save()
	{
		AreaState state = new AreaState();
		
		//save activated objects
		
		for(GameObject go : Game.inst.gameObjectSystem.getObjectsByType(Switch.class))
		{
			Switch sw = (Switch) go;
			
			if(sw.isActivated() && sw.isPermanent())
			{
				state.activatedObjects.add(go.getName());
			}
		}
		return state;
	}
	
	public void load(AreaState state)
	{
		//load activated objects

		for(String name : state.activatedObjects)
		{
			Switch sw = (Switch) Game.inst.gameObjectSystem.getObjectByName(name);
			sw.activate();
		}
	}
	
	public TiledMap map;
	
	int mapHeightPixels;
	int mapWidthPixels;
	
	int mapHeightTiles;
	int mapWidthTiles;
	
	public Path getPath(String name)
	{
		if(!paths.containsKey(name))
			throw new RuntimeException("path " + name + " not found");
		return paths.get(name);
	}
	
	public void update()
	{
		if(circuitGraph != null) circuitGraph.update();
	}
	
	void handleMapProperties()
	{
		MapProperties prop = map.getProperties();
		
		mapHeightPixels = prop.get("height", Integer.class)*Game.PIXELS_PER_TILE;
		mapWidthPixels =  prop.get("width", Integer.class)*Game.PIXELS_PER_TILE;
		
		mapWidthTiles = prop.get("width", Integer.class);
		mapHeightTiles = prop.get("height", Integer.class);

		if(prop.containsKey("music"))
			musicTitle = prop.get("music", String.class);
		
		//currently just used for the NPC meeting room. one way of handling per map initialization logic
		//is to set a key
		if(prop.containsKey("load_npcs"))
			rectMapObjects.addAll(MapUtil.generateNpcMapObjects());		
	}
	
	void handleRoomLayer()
	{
		MapLayer roomLayer = map.getLayers().get("rooms");
		
		for(MapObject mo : roomLayer.getObjects())
		{
			loadRoom(mapHeightPixels, mo);
		}
	}
	
	void handleMaplinkLayer()
	{
		MapLayer maplinkLayer = map.getLayers().get("maplinks");
		
		for(MapObject mo : maplinkLayer.getObjects())
		{
			loadMaplink(mapHeightPixels, mo, mo.getProperties());
		}
	}
	
	void handleDoorLayer()
	{
		//the door object checks its own propertes for maplink capabilities.
		//door objects need to be loaded as a maplink so it will be visible as a destination.
		//since the door will sit on top of the maplink, it should never be touched.
		//
		//if a door is chosen as a destination link, the player will clear the maplink rectangle, again 
		//not touching it and not colliding with door.
		
		MapLayer doorLayer = map.getLayers().get("doors");
		
		if(doorLayer == null) return;

		for(MapObject mo : doorLayer.getObjects())
		{
			loadMaplink(mapHeightPixels, mo, mo.getProperties());
			//make sure door object still gets instantiated.
			rectMapObjects.add(new TilespaceRectMapObject((RectangleMapObject) mo, mapHeightPixels));
		}
	}
	
	void handlePathsLayer()
	{
		MapLayer pathLayer = map.getLayers().get("paths");
		
		if(pathLayer == null) return;
		
		for(MapObject mo : pathLayer.getObjects())
		{
			loadPath((PolylineMapObject) mo);
		}
	}
	
	void handleCircuitLayer()
	{
		MapLayer circuitLayer = map.getLayers().get("circuit");
		
		if(circuitLayer == null) return;
		
		circuitGraph = new CircuitGraph(mapHeightTiles, mapWidthTiles);
		
		for(MapObject mo : circuitLayer.getObjects())
		{
			//first load all torches, instantiate and add vertices to graph
			if(mo instanceof RectangleMapObject)
			{
				LogicGateTorch torch = new LogicGateTorch(new TilespaceRectMapObject((RectangleMapObject) mo, mapHeightPixels));
				circuitGraph.addVertex(torch);
				
				circuitObjects.add(torch);
			}
		}
		
		for(MapObject mo : circuitLayer.getObjects())
		{
			//then load all wires, instantiate and add edges to graph
			if(mo instanceof PolylineMapObject)
			{
				Wire wire = new Wire(new TilespacePolylineMapObject((PolylineMapObject) mo));
				circuitGraph.addEdge(wire);
				
				circuitObjects.add(wire);
			}
		}
	}
	
	public Area(TiledMap map)
	{
		this.map = map;
		
		construct1x1Walls();
		
		handleMapProperties();
		
		handleRoomLayer();
		handleMaplinkLayer();
		handleDoorLayer();
		handlePathsLayer();
		
		handleCircuitLayer();
		
		//default loading logic for all other layers. instantiate game object by type.
		for(MapLayer group : map.getLayers())
		{
			if(group.getName().equals("rooms") || group.getName().equals("doors") ||
			   group.getName().equals("paths") || group.getName().equals("maplinks") ||
			   group.getName().equals("circuit")) continue;
			
			for(MapObject to : group.getObjects())
			{
				MapProperties props = to.getProperties();
				String type = props.get("type", String.class);
								
				//add all layer properties to properties of each object in layer
				props.putAll(group.getProperties());
				
				if(type == null)
				{
					Game.log("object: " + to.getName() + " has null type.");
					continue;
				}
				
				else
				{
					rectMapObjects.add(new TilespaceRectMapObject((RectangleMapObject) to, mapHeightPixels));
				}
			}
		}		
	}
	
	private void loadPath(PolylineMapObject mo)
	{
		paths.put(mo.getName(), new Path(new TilespacePolylineMapObject(mo)));					
	}
	
	private void loadMaplink(int mapHeightPixels, MapObject to,
			MapProperties props) {
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
		
		links.add(link);
	}
	private void loadRoom(int mapHeightPixels, MapObject to) {
		//a room objects location is a list of rectangles. room objects with the same name will be merged into a single room object.
		
		if(!rooms.containsKey(to.getName()))
		{
			//create new room object and add it to map
			
			Room r = new Room();
			r.location.add(MapUtil.tilespaceRect((RectangleMapObject) to, mapHeightPixels));
			r.name = to.getName();
			rooms.put(to.getName(), r);
		}
		else
		{
			//update exiting room object 
			
			rooms.get(to.getName()).location.add(MapUtil.tilespaceRect((RectangleMapObject) to, mapHeightPixels));
		}
	}
	
	/**
	 * instantiates a game object from each map object that represents a game object
	 */
	public void instantiateMapObjects()
	{
		for(TilespaceRectMapObject mo : rectMapObjects)
		{
			Game.inst.gameObjectSystem.addObject(MapUtil.instantiate(mo));
			Vector2 pos = mo.rect.getCenter(new Vector2());
			
			Game.log(String.format("\tname: %s, type: %s, position: %f,%f", mo.name, mo.type, pos.x, pos.y));
		}
		
		Game.inst.gameObjectSystem.addAllObjects(circuitObjects);
				
		Game.inst.gameObjectSystem.handleAdditions();		
	}

	public TilespaceRectMapObject getMapObject(String name)
	{
		for(TilespaceRectMapObject mo : rectMapObjects)
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
		for(Room r : rooms.values())
		{
			for(Rectangle rect : r.location)
				if(rect.contains(pos)) return r;
		}
		return null;
	}	
}
