package com.electricsunstudio.shroudedsun.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.electricsunstudio.shroudedsun.Game;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GameObjectSystem
{
	ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	Map<RenderLayer, ArrayList<GameObject>> renderLayers = new EnumMap<RenderLayer, ArrayList<GameObject>>(RenderLayer.class);
	//collection of objects that render shapes instead of the the usual SpriteBatch
	Map<RenderLayer, ArrayList<ShapeRender>> shapeRenderLayers = new EnumMap<RenderLayer, ArrayList<ShapeRender>>(RenderLayer.class);
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
		
		shapeRenderLayers.put(RenderLayer.floor, new ArrayList<ShapeRender>());
		shapeRenderLayers.put(RenderLayer.groundLevel, new ArrayList<ShapeRender>());
		shapeRenderLayers.put(RenderLayer.aboveGround, new ArrayList<ShapeRender>());
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
	
	public void addAllObjects(Collection<GameObject> gameObjects)
	{
		objectsToAdd.addAll(gameObjects);
	}
	
	public void handleAdditions()
	{
		for(GameObject go : objectsToAdd)
		{
			gameObjects.add(go);
			renderLayers.get(go.renderLayer).add(go);
			if(go instanceof ShapeRender)
				shapeRenderLayers.get(go.renderLayer).add((ShapeRender) go);
			if(go.name != null) nameMap.put(go.name, go);
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
	
	public void render(RenderLayer layer, ShapeRenderer sr)
	{
		for(ShapeRender s : shapeRenderLayers.get(layer))
		{
			s.render(sr);
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
	
	//get object by name but also cast it to the desired type
	public <T> T getObjectByName(String name, Class<T> cls)
	{
		return (T) getObjectByName(name);
	}
	
	public <T> List<T> getObjectsByType(Class<T> cls)
	{
		ArrayList<GameObject> results = new ArrayList<GameObject>();
		for(GameObject go : gameObjects)
		{
			if(cls.isInstance(go))
				results.add(go);
		}
		return (List<T>) results;
	}
        
        public List<GameObject> getObjects()
        {
            return gameObjects;
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
