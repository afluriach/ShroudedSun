package com.electricsunstudio.shroudedsun.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.entity.*;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.*;
import com.electricsunstudio.shroudedsun.objects.environment.*;
import com.electricsunstudio.shroudedsun.physics.Physics;
import com.badlogic.gdx.physics.box2d.*;

public abstract class GameObject 
{
	private static final Map<String, Class<? extends GameObject>> gameObjectTypes = new HashMap<String, Class<? extends GameObject>>(){{
		put("wall", Wall.class);
		put("door", Door.class);
		put("barrier", Barrier.class);
		put("block", Block.class);
		put("floor_switch", FloorSwitch.class);
		put("invisible_floor_switch", InvisibleFloorSwitch.class);
		put("red_enemy", RedEnemy.class);
		put("blue_enemy", BlueEnemy.class);
		put("green_enemy", GreenEnemy.class);
		put("jar", Jar.class);
		put("sign", Sign.class);
		put("facer", Facer.class);
		put("follower", Follower.class);
		put("torch", Torch.class);
		put("torch_walker", TorchWalker.class);
		put("guard", Guard.class);
		put("stationary_npc", StationaryNPC.class);
		put("random_walk_npc", RandomWalkNPC.class);
		put("gold_statue", Statue.class);
		put("dark_cirno", DarkCirno.class);
		put("save_point", SavePoint.class);
		put("chest", TreasureChest.class);
        
		put("map_link", Door.class);
	}};
	
	//for an object type loaded from a Tiled map, prepend this package prefix
	//to get the full path name. i.e. all objects that can be loaded by name
	//will be in this package (including subpackages)
	public static final String basePackage = "com.electricsunstudio.shroudedsun.objects";

	public static Class<?> getObjectClass(String name)
	{
		if(name == null) throw new NullPointerException("null name given");
		if(name.equals("")) throw new IllegalArgumentException("blank name given");

		//name found in class mapping
		if(gameObjectTypes.containsKey(name))
		{
			return gameObjectTypes.get(name);
		}
		//check if a valid class exists with a given name
		//every class loaded by name has to be in the com.ESS.SS.objects package.
		
		//concat object package.
		//the type used in the map could still have a package path
		//i.e. level2.gatekeeper would become
		//com.electricsunstudio.shroudedsun.objects.level2.gatekeeper
		else
		{
			String classPath = basePackage + "." + name;
			
			Class cls = null;
			try {
				cls = Class.forName(classPath);
			} catch (ClassNotFoundException ex) {
				throw new NoSuchElementException("object class " + name + " not found");
			}
			
			if(!GameObject.class.isAssignableFrom(cls))
			{
				//the class is not a subclass of GameObject
				throw new IllegalArgumentException("class " + classPath + " is not derived from GameObject");
			}
			
			return cls;
		}
	}
	
	
	//physics
	protected Body physicsBody;
	boolean collisionThisFrame = false;	
	private Vector2 crntAcceleration;
	
	String name;
	boolean expired = false;
	protected RenderLayer renderLayer = RenderLayer.groundLevel;
	
	public GameObject(TilespaceRectMapObject mo)
	{
		name = mo.name;
	}
	
	public GameObject(String name)
	{
		this.name = name;
	}
	
	public void expire()
	{
		if(!expired)
			onExpire();
		expired = true;
	}
	
	public boolean isExpired()
	{
		return expired;
	}
		
	public static boolean allExpired(Iterable<GameObject> list)
	{
		for(GameObject go : list)
		{
			if(!go.isExpired())
				return false;
		}
		return true;
	}
	
	public Vector2 getCenterPos()
	{
		return physicsBody.getPosition().cpy();
	}
	
	public void setVel(Vector2 vel)
	{
		physicsBody.setLinearVelocity(vel);
	}
	
	public Vector2 getVel()
	{
		return physicsBody.getLinearVelocity().cpy();
	}
	
	public RenderLayer getRenderLayer()
	{
		return renderLayer;
	}
	
	public void setRenderLayer(RenderLayer layer)
	{
		Game.inst.gameObjectSystem.updateRenderLayer(this, layer);
		renderLayer = layer;
	}
	
	public void setPos(Vector2 pos)
	{
		physicsBody.setTransform(pos, physicsBody.getAngle());
	}
	
	public void applyKineticFriction(float uk)
	{
		float impulseMag = physicsBody.getMass()*Physics.GRAVITY*uk*Game.SECONDS_PER_FRAME;
		
		Vector2 impulse = getVel().scl(-impulseMag);
		
		physicsBody.applyLinearImpulse(impulse, getCenterPos(), true);
	}
	
	@Override
	public String toString()
	{
		return String.format("gameobject class: %s, name: %s", this.getClass().getSimpleName(), name);
	}
	
	public void onExpire()
	{
	}

	boolean isObstacle()
	{
		Class<?> c = getClass();
		
		return c == Block.class ||
			   c == Door.class && ((Door)this).isLocked() ||
			   c == Sign.class ||
			   c == Torch.class;
//			   c == Wall.class;
	}

	/**
	 * 
	 * @return returns the axis aligned bounding box based on the physics body.
	 */
	public Rectangle getAABB()
	{
		//start with a rectangle of size 0,0 centered at the center of this body.
		//Since fixture coordinates are relative to center, start with the center of
		//the rectangle at the origin. 
		Vector2 center = Vector2.Zero;
		Rectangle box = new Rectangle();
		box.setCenter(center);
		box.height = 0;
		box.width = 0;
		
		for(Fixture f : physicsBody.getFixtureList())
		{
			if(f.getShape().getType() == Shape.Type.Polygon)
			{
				//consider each vertex in the polygon. if a point is not in the AABB, 
				//expand it to include that point.
				PolygonShape s = (PolygonShape) f.getShape();
				Vector2 vertex = new Vector2();
				
				for(int i=0;i<s.getVertexCount(); ++i)
				{
					//vertex coordinates are relative to the center of the body. Or center of fixture(?)
					//in this case, only one fixture centered on the body. 
					s.getVertex(i, vertex);
					
					if(vertex.x < box.x)
					{
						box.width += box.x - vertex.x;
						box.x = vertex.x;
					}
					else if(vertex.x > box.x + box.width)
					{
						box.width = vertex.x - box.x;
					}
					
					if(vertex.y < box.y)
					{
						box.height += box.y - vertex.y;
						box.y = vertex.y;
					}
					else if(vertex.y > box.y + box.height)
					{
						box.height = vertex.y - box.y;
					}
				}
			}
			else if(f.getShape().getType() == Shape.Type.Circle)
			{
				CircleShape s = (CircleShape) f.getShape();
				
				//consider bounding square defining the circle.
				//check each of the four edges based on the four axis-aligned
				//points on the circle
				
				if(center.x - s.getRadius() < box.x)
				{
					box.width += box.x - (center.x - s.getRadius());
					box.x = center.x - s.getRadius();
				}
				if(center.x + s.getRadius() > box.x + box.width)
				{
					box.width = center.x + s.getRadius() - box.x;
				}
				
				if(center.y - s.getRadius() < box.y)
				{
					box.height += box.y - (center.y - s.getRadius());
					box.y = center.y - s.getRadius();
				}
				if(center.y + s.getRadius() > box.y + box.height)
				{
					box.height = center.y + s.getRadius() - box.y;
				}
				
			}
			else
			{
				throw new IllegalArgumentException("unsupported fixture shape: " + f.getShape().getType());
			}
				
		}
		
		//translate AABB based on the center of the GO		
		box.setCenter(getCenterPos());
		return box;
	}	
	
	public String getName()
	{
		return name;
	}
	
	public void setAccel(Vector2 acc)
	{
		crntAcceleration = acc;
	}
	
	public void applyAccel()
	{
		if(crntAcceleration != null)
		{
			Vector2 dv = crntAcceleration.cpy().scl(Game.SECONDS_PER_FRAME);
			setVel(getVel().add(dv));			
		}
	}
	
	/**
	 * set each of the GameObjects fixtures to 
	 * @param sensor the value to set. if true, fixtures will still register collisions but will not have any solid physical presence.
	 */
	public void setSensor(boolean sensor)
	{
		for(Fixture f : physicsBody.getFixtureList())
		{
			f.setSensor(sensor);
		}
	}
	
	public abstract void update();
	public abstract void render(SpriteBatch sb);	
	public abstract void handleContact(GameObject other);
	public abstract void handleEndContact(GameObject other);
	public abstract void init();
}
