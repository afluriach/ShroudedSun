package com.pezventure.objects;

import java.util.ArrayList;
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
	
	public GameObjectSystem()
	{
		renderLayers.put(RenderLayer.floor, new ArrayList<GameObject>());
		renderLayers.put(RenderLayer.ground, new ArrayList<GameObject>());
	}
		
	public void addObject(GameObject go)
	{
		gameObjects.add(go);
		renderLayers.get(go.renderLayer).add(go);
		nameMap.put(go.name, go);
	}
	
	public void updateAll()
	{
		for(GameObject go : gameObjects)
		{
			go.update();
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
		return nameMap.get(name);
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
