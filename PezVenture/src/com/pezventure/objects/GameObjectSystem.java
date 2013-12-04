package com.pezventure.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.physics.Physics;

public class GameObjectSystem
{
	ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	Map<RenderLayer, ArrayList<GameObject>> renderLayers = new TreeMap<RenderLayer, ArrayList<GameObject>>();
	Map<String, GameObject> nameMap = new TreeMap<String, GameObject>();
	ArrayList<GameObject> objectsToAdd = new ArrayList<GameObject>();
	
	public void clear()
	{
		gameObjects.clear();
		for(ArrayList<GameObject> renderLayer : renderLayers.values())
		{
			renderLayer.clear();
		}
		nameMap.clear();
	}
	
	public GameObjectSystem()
	{
		renderLayers.put(RenderLayer.floor, new ArrayList<GameObject>());
		renderLayers.put(RenderLayer.above_floor, new ArrayList<GameObject>());
	}
		
	/**
	 * Adds GameObject before the next update cycle. Additions are not handled within the update cycle,
	 * as this creates concurrent modification problems.
	 * @param go
	 */
	public void addObject(GameObject go)
	{
		objectsToAdd.add(go);
	}
	
	public void handleAdditions()
	{
		for(GameObject go : objectsToAdd)
		{
			gameObjects.add(go);
			renderLayers.get(go.renderLayer).add(go);
			nameMap.put(go.name, go);
		}
		objectsToAdd.clear();
	}
	
	private void remove(GameObject go)
	{
		gameObjects.remove(go);
		renderLayers.get(go.renderLayer).remove(go);
		nameMap.remove(go.name);
		
		Game.inst.physics.removeBody(go.physicsBody);
	}

	
	public void updateAll()
	{
		for(GameObject go : gameObjects)
		{
			go.update();
		}
	}
	
	public void removeExpired()
	{
		ArrayList<GameObject> expired = new ArrayList<GameObject>();
		
		for(GameObject go : gameObjects)
		{
			if(go.expired)
			{
				expired.add(go);
			}
		}
		
		for(GameObject go : expired)
		{
			Game.inst.physics.handleEndContact(go);
			remove(go);
		}
	}
		
	public void render(RenderLayer layer, SpriteBatch sb)
	{
		for(GameObject go : renderLayers.get(layer))
		{
			go.render(sb);
		}
	}
	
	public GameObject getObjectByName(String name)
	{
		if(!nameMap.containsKey(name))
			throw new RuntimeException(String.format("object %s not found", name));
		
		return nameMap.get(name);
	}
	
	public ArrayList<GameObject> getObjectsByType(Class<?> cls)
	{
		ArrayList<GameObject> results = new ArrayList<GameObject>();
		for(GameObject go : gameObjects)
		{
			if(cls.isInstance(go))
				results.add(go);
		}
		return results;
	}
	
//	public void renderPhysicsShapes(ShapeRenderer sr)
//	{
//		for(GameObject go : gameObjects)
//		{
//			go.physicsShape.render(sr);
//		}
//	}
	
//	public void updatePhysics()
//	{
//		checkCollisions();
//		Physics.inst().
//		for(GameObject go : gameObjects)
//		{
//			//apply velocity
//			Vector2 dv = go.vel.cpy().mul(Game.SECONDS_PER_FRAME);
//			go.physicsShape.setCenter(go.physicsShape.getCenter().add(dv));
//		}
//	}
	
//	public static boolean checkCollision(Shape a, Shape b)
//	{
//		if(a instanceof RectShape && b instanceof RectShape)
//		{
//			return a.getAABB().intersects(b.getAABB());
//		}
//		else if(a instanceof CircShape && b instanceof CircShape)
//		{
//			return a.getBoundingCircle().intersects(b.getBoundingCircle());
//		}
//		else
//		{
//			RectShape rect = (a instanceof RectShape) ? a.getAABB() : b.getAABB();
//			CircShape circ = (a instanceof CircShape) ? a.getBoundingCircle() : b.getBoundingCircle();
//			
//			return rect.contains(circ.x, circ.y) ||
//				   circ.intersectsLine(rect.x, rect.y, rect.x+rect.width, rect.y) ||
//				   circ.intersectsLine(rect.x, rect.y, rect.x, rect.y+rect.height) ||
//				   circ.intersectsLine(rect.x, rect.y+rect.height, rect.x+rect.width, rect.y+rect.height) ||
//				   circ.intersectsLine(rect.x+rect.width, rect.y, rect.x+rect.width, rect.y+rect.height);
//		}
//		
//	}
//	
//	public void checkCollisions()
//	{
//		for(int i=0, size=gameObjects.size(); i < size; ++i)
//		{
//			for(int j=i+1; j < size; ++j)
//			{
//				GameObject a = gameObjects.get(i);
//				GameObject b = gameObjects.get(j);
//				
//				if(checkCollision(a.physicsShape, b.physicsShape))
//				{
//					a.handleCollision(b);
//					b.handleCollision(a);
//				}
//			}
//		}
//	}

}
