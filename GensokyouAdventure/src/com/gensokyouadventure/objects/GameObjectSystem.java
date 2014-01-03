package com.gensokyouadventure.objects;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.gensokyouadventure.Game;

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
		objectsToAdd.clear();
	}
	
	public GameObjectSystem()
	{
		renderLayers.put(RenderLayer.floor, new ArrayList<GameObject>());
		renderLayers.put(RenderLayer.groundLevel, new ArrayList<GameObject>());
		renderLayers.put(RenderLayer.aboveGround, new ArrayList<GameObject>());
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
	
	public void initAll()
	{
		for(GameObject go : gameObjects)
		{
			go.init();
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
		
		//if a gameobject is touching another when it is expiring, the physics engine will not register an end contact
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
	
	//check if the object is still in existence and not expired
	public boolean hasObject(String name)
	{
		if(!nameMap.containsKey(name))
			return false;
		
		return !nameMap.get(name).isExpired();
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
		
	public ArrayList<Rectangle> getObstacles()
	{
		ArrayList<Rectangle> obs = new ArrayList<Rectangle>();
		for(GameObject go : gameObjects)
		{
			if(go.isObstacle())
			{
				obs.add(go.getAABB());
			}
		}
		return obs;
	}
	
	public void applyAccel()
	{
		for(GameObject go : gameObjects)
		{
			go.applyAccel();
		}
	}
	
	public void updateRenderLayer(GameObject go, RenderLayer newLayer)
	{
		renderLayers.get(go.renderLayer).remove(go);
		go.renderLayer = newLayer;
		renderLayers.get(newLayer).add(go);
	}
	
	public boolean allExpired(String[] names)
	{
		for(String name : names)
		{
			if (hasObject(name)) return false;
		}		
		return true;
	}
}
