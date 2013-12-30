package com.pezventure.map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.IVector2;
import com.pezventure.PathSegment;


//each node is represented by a tile. tile coordinates are considered based on lower edge. i.e. coord 0.5, 0.5 is the center of the tile 0,0.
//
//tilenode postions are derived from truncating float unit/tile coordinates to int tile coordinates. note: since negative coordinates are not used, this should be ok. 
//otherwise Math.floor would need to be used.
//
//tilenode's positions are based on tile center. thus all path segment nodes will be at a tile center. each tile is connected to adjacent tiles that
//are occupiable. 8 directions are considered, the four primary directions (1 unit of distance between center), and four diagonal directions (sqrt(2) units of distance between centers). 
public class TileGraph
{
	private static final float sqrt2 = (float) Math.sqrt(2);
	private static final Vector2 edgeToCenter = new Vector2(0.5f, 0.5f);
	
	//disjoint sets. used to determine which tiles are reachable from a given starting point
	int[][] tilePartitions;
	boolean[][] tiles;
	int width;
	int height;
	TiledMap map;
	
	public TileGraph(Area area)
	{
		map = area.map;
		
		this.width = map.getProperties().get("width", Integer.class);
		this.height = map.getProperties().get("width", Integer.class);;
		
		tiles = new boolean[height][width];
		
		refresh();
	}
	
	private void blankTiles()
	{
		for(int i=0;i<height; ++i)
		{
			for(int j=0;j<width; ++j)
			{
				tiles[i][j] = true;
			}
		}
	}
	
	public void refresh()
	{
		blankTiles();
		markUnoccupiableTiles();
	}
	
	private void calculatePartitions()
	{
		int partitionID = 0;
		
		for(int i=0;i<height; ++i)
		{
			for(int j=0;j<width; ++j, ++partitionID)
			{
				if(!tiles[i][j]) tilePartitions[i][j] = -1;
				
				else tilePartitions[i][j] = partitionID;
			}
		}
		
		for(int i=0;i<height; ++i)
		{
			for(int j=0;j<width; ++j)
			{
				//do not attempt to join tile if it is not occupiable
				if(!tiles[i][j]) continue;
				
				if(j < width - 1)
				{
					//attempt to join partition rightward.
					if(tiles[i][j+1]) tilePartitions[i][j+1] = tilePartitions[i][j];
				}
				if(i < height -1)
				{
					//attempt to join partition upward
					if(tiles[i+1][j]) tilePartitions[i+1][j] = tilePartitions[i][j];
				}
				if(j < width - 1 && i < height -1)
				{
					//attempt to join diagonally. 
					if(tiles[i+1][j] && tiles[i][j+1]) tilePartitions[i+1][j+1] = tilePartitions[i][j];
				}
			}
		}
	}

	private void markUnoccupiableTiles() {
		//-1 means tile cannot be occupied and will not be considered a node in the graph
		//if there is no background (ground texture) drawn, or if there is a wall at the given tile
		//a raycast should also find walls, but if there is a wall finding it in tilemap should be faster
		//
		//a tile is also invalid if it is occupied by a stationary object obstacle, such as a block or torch. 
		//some objects like a jar or the player would be pushed out of the way as part of pathfinding. 
		
		TiledMapTileLayer wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");
		TiledMapTileLayer groundLayer = (TiledMapTileLayer) map.getLayers().get("background");
		
		for(int i=0;i<height; ++i)
		{
			for(int j=0;j<width; ++j)
			{
				if(wallLayer.getCell(j, i) != null || groundLayer.getCell(j, i) == null)
					tiles[i][j] = false;
			}
		}
		
		//mark tiles occupied by environmental objects as inaccessible
		
		for(Rectangle r : Game.inst.gameObjectSystem.getObstacles())
		{
			//integer range of tiles to be marked. 
			for(int i=(int) r.y; i < Math.ceil(r.y + r.height) && i < height; ++i)
			{
				for(int j=(int) r.x; j < Math.ceil(r.x + r.width) && j < width; j++)
				{
					tiles[i][j] = false;
				}
			}
		}
	}
	
	//select a random, occupiable tile within a certain distance of a target location and find
	//a path to that location.
	//
	//this assumes all occupiable tiles are reachable. only search within current room.
	public List<PathSegment> radiusSearch(Vector2 start, Vector2 target, float minDist, float maxDist, Rectangle crntRoom)
	{
		ArrayList<IVector2> destinations = new ArrayList<IVector2>();
		Gdx.app.log(Game.TAG, String.format("radius search. start: %f,%f, target: %f,%f, min: %f, max: %f, room: %f,%f to %f,%f", start.x, start.y, target.x, target.y, minDist, maxDist, crntRoom.x, crntRoom.y, crntRoom.x+crntRoom.width, crntRoom.y+crntRoom.height));
		
		for(int y = (int) (start.y - maxDist); y < start.y + maxDist; ++y)
		{
			if(y < crntRoom.y || y > crntRoom.y + crntRoom.height) continue;
			if(y < 0 || y >= height) continue;
			
			for(int x = (int) (start.x - maxDist); x < start.x + maxDist; ++x)
			{
				if(x < crntRoom.x || x > crntRoom.x + crntRoom.width) continue;
				if(x < 0 || x >= width) continue;
				
				float distToTarget = target.cpy().sub(new Vector2(x+0.5f,y+0.5f)).len();
				
//				System.out.println(String.format("checking tile: %d,%d, dist: %f", x, y, distToTarget));
				
				if(tiles[y][x] && distToTarget >= minDist && distToTarget <= maxDist)
					destinations.add(new IVector2(x,y));
			}
		}
		
		if(destinations.isEmpty())
		{
			Gdx.app.log(Game.TAG, "Radius search, no path found.");
			return null;
		}
		
		IVector2 dest = destinations.get(Game.inst.random.nextInt(destinations.size()));
		Gdx.app.log(Game.TAG, String.format("Radius search, path to %d,%d.", dest.x, dest.y));
		
		return dijkstras(new IVector2(start), dest);
	}
	
	public List<PathSegment> getPath(Vector2 start, Vector2 end)
	{
		Gdx.app.log(Game.TAG, String.format("Get path from %f,%f to %f,%f.", start.x, start.y, end.x, end.y));
		
		
		if(!tiles[(int) start.y][(int) start.x])
			throw new RuntimeException(String.format("Start position %f,%f, tilepos %d,%d is unoccupiable on graph.", start.x, start.y, (int) start.x, (int) start.y));
		if(!tiles[(int) end.y][(int) end.x])
			throw new RuntimeException(String.format("End position %f,%f, tilepos %d,%d is unoccupiable on graph.", end.x, end.y, (int) end.x, (int) end.y));
		
		return dijkstras(new IVector2(start), new IVector2(end));
	}
	
	private List<PathSegment> dijkstras(IVector2 start, IVector2 end)
	{
		//distance to each tilenode
		float [][] costs = new float[height][width];
		//saves the coord of the tile that leads to this one, associated with the 
		//best path currently stored.
		//start tile, as well as any unreached tiles will remain null
		//would be more efficient to store direction. only 8 possible directions. could store in a char/byte
		IVector2 [][] prev = new IVector2[height][width];
		PriorityQueue<TileVertex> queue = new PriorityQueue<TileVertex>();
		
		for(int i=0;i<height; ++i)
		{
			for(int j=0;j<width; ++j)
			{
				costs[i][j] = Float.MAX_VALUE;
			}
		}
		
		costs[start.y][start.x] = 0;
		
		queue.add(new TileVertex(start.x, start.y, 0f));
		
		while(!queue.isEmpty())
		{
			TileVertex crnt = queue.remove();
			
			if(crnt.x < 0 || crnt.x >= width || crnt.y < 0 || crnt.y >= height)
			{
				Gdx.app.log(Game.TAG, String.format("Graph search. Invalid node %d,%d.", crnt.x, crnt.y));
				continue;
			}
			
			//consider all 8 potential adjacent tilenodes. loop through the nine-patch (3x3) tiles centered at the current node as a 2d array
			//if cost to node from this one is less than previously recorded, update cost, add to queue.
			//store this node if it leads to an adjacent node. used to derive path.
			for(int i=-1; i <= 1; ++i)
			{
				//do not go over the edge and out of array bounds in the y direction
				if( (i < 0 && crnt.y == 0) || 
					(i > 0 && crnt.y > width - 2)) continue;
				
				for(int j=-1; j <= 1; ++j)
				{
					//do not check this (same tile)
					if(i == 0 && j == 0) continue;
					
					//do not go over the edge and out of array bounds in the x direction.
					if( (j < 0 && crnt.x == 0) ||
					    (j > 0 && crnt.x > width-2)) continue;
					
					//if both are non-zero, adjacent tile is diagonal. otherwise it is adjacent in a primary direction
					float adjCost = (i != 0 &&  j != 0) ? sqrt2 : 1f;
					
					//check that the tile is occupiable. in order to move diagonally to another tile,
					//both adjacent tiles in the primary direction must be occupiable, since such a path
					//takes the entity through the corner of the four tiles.
					
					//this will check the three tiles in the case of diagonal movement. In the case of horizontal movement,
					//i == 0, j != 0, the second term will check the current tile, and the third term will check the 
					//target tile again, and vice versa for vertical movement. 
					boolean occupiable = tiles[crnt.y+i][crnt.x+j] && tiles[crnt.y+i][crnt.x] && tiles[crnt.y][crnt.x+j];
				
					if(occupiable && crnt.cost + adjCost < costs[crnt.y+i][crnt.x+j])
					{
						costs[crnt.y+i][crnt.x+j] = crnt.cost + adjCost;
						prev[crnt.y+i][crnt.x+j] = new IVector2(crnt.x, crnt.y);
						
						queue.add(new TileVertex(crnt.x+j, crnt.y+i, costs[crnt.y+i][crnt.x+j]));	
					}
				}
			}
		}
		
		if(costs[end.y][end.x] == Float.MAX_VALUE)
		{
			//end node was not reachable
			return null;
		}
		else
		{
			//Compute path segments. Start from the end and construct path in reverse, by
			//following the previous tile. 
			LinkedList<PathSegment> segments = new LinkedList<PathSegment>();
			
			IVector2 crntPos = end;
						
			while(!crntPos.equals(start)) //construct path segment loop
			{
				//for the first hop, the segment has 0 length and no direction.
				//follow the direction to the prev tile unconditionally
				//then, continue along path and extend beginning of segment as 
				//long as the direction remains the same

				//find previous tile and compute direction as a delta x and y.
				IVector2 segmentFirstPrevTile = prev[crntPos.y][crntPos.x];
				IVector2 segmentDirection = new IVector2(crntPos.x - segmentFirstPrevTile.x, crntPos.y - segmentFirstPrevTile.y);
				
				//convert tile (edge) coords to center coords. update crntPos to include the first hop of the path segment
				Vector2 segmentEndPos = crntPos.getFloat().add(edgeToCenter);
				Vector2 segmentStartPos = segmentFirstPrevTile.getFloat().add(edgeToCenter);
				crntPos = segmentFirstPrevTile;
				
				while(!crntPos.equals(start)) //extend path segment loop
				{
					//look at previous tile and compute direction. if it is the same as the segment
					//segment, extend and loop, else break
					
					IVector2 prevTile = prev[crntPos.y][crntPos.x];
					IVector2 prevDir = new IVector2(crntPos.x - prevTile.x, crntPos.y - prevTile.y);
					
					if(prevDir.equals(segmentDirection))
					{
						crntPos = prevTile;
						segmentStartPos = crntPos.getFloat().add(edgeToCenter);
					}
					else
					{
						break;
					}
				}
				//create path segment and add it to beginning of list
				segments.add(0, new PathSegment(segmentStartPos, segmentEndPos));

			}
			return segments;
		}
	}
	
	//helper class used in dijkstras. stores a tilenode (by tile coordinate) and cost to that node.
	class TileVertex implements Comparable<TileVertex>
	{
		//avoid using a vector object to save space. 
		int x, y;
		float cost;
		
		public TileVertex(int x, int y, float cost)
		{
			this.x = x;
			this.y = y;
			this.cost = cost;
		}
		
		public int compareTo(TileVertex o)
		{
			if(this.cost < o.cost) return -1;
			else if(this.cost == o.cost) return 0;
			else return 0;
		}
	}	
}
