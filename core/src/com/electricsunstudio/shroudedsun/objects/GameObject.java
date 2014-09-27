package com.electricsunstudio.shroudedsun.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.entity.RandomWalkNPC;
import com.electricsunstudio.shroudedsun.objects.entity.StationaryNPC;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.BlueEnemy;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Facer;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Follower;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.GreenEnemy;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Guard;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.DarkCirno;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.RedEnemy;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.TorchWalker;
import com.electricsunstudio.shroudedsun.objects.environment.Barrier;
import com.electricsunstudio.shroudedsun.objects.environment.Block;
import com.electricsunstudio.shroudedsun.objects.environment.Door;
import com.electricsunstudio.shroudedsun.objects.environment.FloorSwitch;
import com.electricsunstudio.shroudedsun.objects.environment.InvisibleFloorSwitch;
import com.electricsunstudio.shroudedsun.objects.environment.Jar;
import com.electricsunstudio.shroudedsun.objects.environment.SavePoint;
import com.electricsunstudio.shroudedsun.objects.environment.Sign;
import com.electricsunstudio.shroudedsun.objects.environment.Statue;
import com.electricsunstudio.shroudedsun.objects.environment.Torch;
import com.electricsunstudio.shroudedsun.objects.environment.TreasureChest;
import com.electricsunstudio.shroudedsun.objects.environment.Wall;
import com.electricsunstudio.shroudedsun.objects.interaction.Grabbable;
import com.electricsunstudio.shroudedsun.physics.Physics;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.electricsunstudio.shroudedsun.objects.Gatekeeper;
import com.electricsunstudio.shroudedsun.objects.Level2Sensor;
import com.electricsunstudio.shroudedsun.objects.Level2Spirit;

public abstract class GameObject 
{
	private static Map<String, Class<? extends GameObject>> gameObjectTypes = new HashMap<String, Class<? extends GameObject>>();
	private static boolean objectTypesInitialized = false;
	
	public static void addClass(String str, Class<? extends GameObject> cls)
	{
		gameObjectTypes.put(str,  cls);
	}
	
	public static void addTypes()
	{	
		addClass("wall", Wall.class);
		addClass("door", Door.class);
		addClass("barrier", Barrier.class);
		addClass("block", Block.class);
		addClass("floor_switch", FloorSwitch.class);
		addClass("invisible_floor_switch", InvisibleFloorSwitch.class);
		addClass("red_enemy", RedEnemy.class);
		addClass("blue_enemy", BlueEnemy.class);
		addClass("green_enemy", GreenEnemy.class);
		addClass("jar", Jar.class);
		addClass("sign", Sign.class);
		addClass("facer", Facer.class);
		addClass("follower", Follower.class);
		addClass("torch", Torch.class);
		addClass("torch_walker", TorchWalker.class);
		addClass("guard", Guard.class);
		addClass("stationary_npc", StationaryNPC.class);
		addClass("random_walk_npc", RandomWalkNPC.class);
		addClass("gold_statue", Statue.class);
		addClass("dark_cirno", DarkCirno.class);
		addClass("save_point", SavePoint.class);
		addClass("chest", TreasureChest.class);
        
        addClass("level2_gatekeeper", Gatekeeper.class);
        addClass("level2_sensor", Level2Sensor.class);
        addClass("level2_spirit", Level2Spirit.class);
		
		addClass("map_link", Door.class);
	}
	
	public static Class<?> getObjectClass(String name)
	{
		if(name == null) throw new NullPointerException("null name given");
		if(name.equals("")) throw new IllegalArgumentException("blank name given");
		
		if(!objectTypesInitialized)
		{
			addTypes();
			objectTypesInitialized = true;
		}
		if(!gameObjectTypes.containsKey(name))
		{
			throw new NoSuchElementException("unknow object type:" + name);
		}
		return gameObjectTypes.get(name);
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
//		physicsBody = Physics.inst().addRectBody(pos, height, width, this);
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
	
	public String toString()
	{
		return String.format("gameobject class: %s, name: %s", this.getClass().getSimpleName(), name);
	}
	
	public void onExpire()
	{
	}
	
	/**
	 * 
	 * @return Is this object any category of object that the player can interact with.
	 */
	public boolean canPlayerInteract()
	{
		return this instanceof Grabbable && ((Grabbable)this).canGrab() ||
		       this instanceof Sign;
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
