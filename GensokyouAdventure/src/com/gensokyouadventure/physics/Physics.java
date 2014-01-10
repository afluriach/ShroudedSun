package com.gensokyouadventure.physics;

import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Array;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.objects.GameObject;

public class Physics
{
	public static final int VELOCITY_ITERATIONS = 8;
	public static final int POSITION_ITERATIONS = 3;
	
	public static final float DEFAULT_MASS = 1.0f;
	public static final float GRAVITY = 9.8f;
	
	public static final short enemyCategory            = 1;
	public static final short enemyBulletCategory      = 2;
	public static final short playerCategory 		   = 4;
	public static final short playerBulletCategory 	   = 8;
	public static final short playerShieldCategory 	   = 16;
	public static final short onFloorCategory          = 32;
	public static final short entityHeightCategory     = 64;
	public static final short environmentCateogry      = 128;
	public static final short sensorCategory           = 256;
	public static final short npcCategory              = 512;
	
	public static Map<String, Filter> collisionFilters;
	
	public static void loadFilters()
	{
		Filter filter;
		collisionFilters = new TreeMap<String, Filter>();
		
		//enemy
		filter = new Filter();
		filter.categoryBits = enemyCategory;
		filter.maskBits = playerCategory | playerBulletCategory | playerShieldCategory | onFloorCategory | entityHeightCategory | environmentCateogry | sensorCategory; 
		collisionFilters.put("enemy", filter);
		
		//enemy bullet
		filter = new Filter();
		filter.categoryBits = enemyBulletCategory;
		filter.maskBits = playerCategory | playerShieldCategory | entityHeightCategory | environmentCateogry | sensorCategory; 
		collisionFilters.put("enemy_bullet", filter);
		
		//player
		filter = new Filter();
		filter.categoryBits = playerCategory;
		filter.maskBits = enemyCategory | enemyBulletCategory | onFloorCategory | entityHeightCategory | environmentCateogry | sensorCategory | npcCategory;
		collisionFilters.put("player", filter);
		
		//player bullet
		filter = new Filter();
		filter.categoryBits = playerBulletCategory;
		filter.maskBits = enemyCategory | entityHeightCategory | environmentCateogry | sensorCategory;
		collisionFilters.put("player_bullet", filter);
		
		//player shield
		filter = new Filter();
		filter.categoryBits = playerShieldCategory;
		filter.maskBits = enemyCategory | enemyBulletCategory | entityHeightCategory | sensorCategory;
		collisionFilters.put("player_shield", filter);
		
		//floor object
		filter = new Filter();
		filter.categoryBits = onFloorCategory;
		filter.maskBits = playerCategory | enemyCategory | onFloorCategory | sensorCategory | environmentCateogry;
		collisionFilters.put("floor", filter);
		
		//environmental object
		filter = new Filter();
		filter.categoryBits = environmentCateogry;
		filter.maskBits = playerCategory | playerBulletCategory | enemyCategory | enemyBulletCategory | environmentCateogry | onFloorCategory | sensorCategory;
		collisionFilters.put("environmental_floor", filter);
		
		//environmental hovering object
		filter = new Filter();
		filter.categoryBits = environmentCateogry;
		filter.maskBits = playerCategory | playerBulletCategory | enemyCategory | enemyBulletCategory | environmentCateogry | sensorCategory;
		collisionFilters.put("environmental_hovering", filter);
		
		//npc
		filter = new Filter();
		filter.categoryBits = npcCategory;
		filter.maskBits = playerCategory | environmentCateogry | sensorCategory | npcCategory | onFloorCategory | entityHeightCategory | sensorCategory;
		collisionFilters.put("npc", filter);
				
		//wall
		filter = new Filter();
		filter.categoryBits = environmentCateogry;
		filter.maskBits = playerCategory | playerBulletCategory | enemyCategory | enemyBulletCategory | environmentCateogry | entityHeightCategory | sensorCategory;
		collisionFilters.put("wall", filter);
		
		//player sensor
		collisionFilters.put("player_sensor", sensorFilter(playerCategory));
		
		//player bullet sensor
		collisionFilters.put("player_bullet_sensor", sensorFilter(playerBulletCategory));
		
		//targeting sensor
		filter = new Filter();
		filter.categoryBits = sensorCategory;
		filter.maskBits = enemyCategory | npcCategory;
		collisionFilters.put("targeting_sensor", filter);
	}
	
	public static Filter sensorFilter(short target)
	{
		Filter filter = new Filter();
		filter.categoryBits = sensorCategory;
		filter.maskBits = target;
		return filter;
	}
	
	World world;
	
	Box2DDebugRenderer box2dRenderer = new Box2DDebugRenderer();
	ContactHandler contactHandler;
		
	public Physics()
	{
		//no gravity, allow sleeping objects
		world = new World(new Vector2(0,0), true);
		
		contactHandler = new ContactHandler();
		
		box2dRenderer.setDrawAABBs(true);
		box2dRenderer.setDrawBodies(true);
		box2dRenderer.setDrawInactiveBodies(true);
		
		world.setContactListener(contactHandler);
		
		if(collisionFilters == null)
			loadFilters();
		
	}
	
	public void removeBody(Body b)
	{
		world.destroyBody(b);
	}
	
	public void clear()
	{
		Game.log("clearing physics");
		world.dispose();
		world = new World(new Vector2(0,0), true);
		world.setContactListener(contactHandler);
		Game.log( "new world");
	}
	
	public void update()
	{
		//should be handled by contact handler instead
//		handleCollisions();
		Game.inst.gameObjectSystem.applyAccel();		
		world.step(Game.SECONDS_PER_FRAME,VELOCITY_ITERATIONS, POSITION_ITERATIONS);		
	}
	
	public void handleCollisions()
	{
		for(Contact contact : world.getContactList())
		{
			//AABB overlap makes a contact but does not necessarily
			//mean a collision
			if(!contact.isTouching()) continue;
			
			Body bodyA = contact.getFixtureA().getBody();
			Body bodyB = contact.getFixtureB().getBody();
			
			GameObject objA = (GameObject) bodyA.getUserData();
			GameObject objB = (GameObject) bodyB.getUserData();
			
			objA.handleContact(objB);
			objB.handleContact(objA);			
		}
	}
	
	private static boolean contactInvolvesObject(Contact c, GameObject go)
	{
		return c.getFixtureA().getBody().getUserData() == go ||
			   c.getFixtureB().getBody().getUserData() == go;
	}
	
	//if a gameobject expires, endContact needs to be processed 
	public void handleEndContact(GameObject go)
	{
		for(Contact contact : world.getContactList())
		{
			if(contact.isTouching() && contactInvolvesObject(contact, go))
			{
				GameObject a = (GameObject) contact.getFixtureA().getBody().getUserData();
				GameObject b = (GameObject) contact.getFixtureB().getBody().getUserData();
				
				a.handleEndContact(b);
				b.handleEndContact(a);
			}
		}
	}
	
	public GameObject closestObjectFeeler(Vector2 startingPos, float angleDeg, float distance, Class<?> targetCls)
	{
		Vector2 feeler = Util.ray(angleDeg, distance);
		Vector2 endPos = startingPos.cpy().add(feeler);
		
		FeelerRaycastCallback cb = new FeelerRaycastCallback(targetCls);
		world.rayCast(cb, startingPos, endPos);
		
		return cb.getResult();
	}
	
	
	
	public float distanceFeeler(Vector2 startingPos, float angleDeg, float distance, Class<?> targetCls)
	{
		Vector2 feeler = Util.ray(angleDeg, distance);
		Vector2 endPos = startingPos.cpy().add(feeler);

		FeelerRaycastCallback cb = new FeelerRaycastCallback(targetCls);
		world.rayCast(cb, startingPos, endPos);
		
		if(cb.getResult() != null)
			return cb.getFeelerDistFraction()*feeler.len();
		else
		{
			//fraction will be 0. just return length
			return feeler.len();
		}
	}
	
	/**
	 * 
	 * @param startingPos position where the raycast starts
	 * @param target target object
	 * @return whether there exists a line-of-sight from starting point to center of target object
	 */
	public boolean lineOfSight(Vector2 startingPos, GameObject target)
	{
		LineOfSightRaycastCallback cb = new LineOfSightRaycastCallback(target);
		world.rayCast(cb, startingPos, target.getCenterPos());
		
		return cb.hitTarget();
	}
	
	public Body addRectBody(Rectangle rect, GameObject ref, BodyType type, String filter)
	{
		return addRectBody(rect.getCenter(new Vector2()), rect.height, rect.width, type, ref, filter);
	}

	public Body addRectBody(Rectangle rect, GameObject ref, BodyType type, float mass, boolean sensor, String filter)
	{
		return addRectBody(rect.getCenter(new Vector2()), rect.height, rect.width, type, ref, mass, sensor, filter);
	}
	
	public Body addRectBody(Vector2 pos,float height, float width, BodyType type, GameObject ref, String filter)
	{
		return addRectBody(pos, height, width, type, ref, DEFAULT_MASS, false, filter);
	}

	
	public Body addRectBody(Vector2 pos,
			                float height,
			                float width, 
			                BodyType type,
			                GameObject ref,
			                float mass,
			                boolean sensor,
			                String filter)
	{
		float area = height*width;
		float density = mass/area;
		
		BodyDef bd = new BodyDef();
		
		bd.type = type;
		bd.fixedRotation = true;
		bd.position.set(pos);
		
		Body b = world.createBody(bd);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2, height/2);
		
		Fixture f = b.createFixture(shape, density);
		f.setSensor(sensor);
		
		b.setUserData(ref);
		b.resetMassData();

		f.setFilterData(collisionFilters.get(filter));		
		
		shape.dispose();
		
		return b;
	}
	
	public Body addCircleBody(Vector2 pos, float radius, BodyType type, GameObject ref, float mass, boolean sensor, String filter)
	{
		float area = (float) (Math.PI*radius*radius);
		float density = mass/area;
		
		BodyDef bd = new BodyDef();
		
		bd.type = type;
		bd.fixedRotation = true;
		bd.position.set(pos);
		
		Body b = world.createBody(bd);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		
		Fixture f = b.createFixture(shape, density);
		f.setSensor(sensor);
		f.setFilterData(collisionFilters.get(filter));
		
		b.setUserData(ref);
		b.resetMassData();
		
		shape.dispose();
		
		return b;
	}
		
	public void debugRender(Matrix4 combined)
	{
		box2dRenderer.render(world, combined.cpy().scale(32, 32, 1));
	}
	
	public Joint joinBodies(Body a, Body b)
	{
		DistanceJointDef def = new DistanceJointDef();
		def.initialize(a, b, a.getPosition(), b.getPosition());
//		
//		WeldJointDef def = new WeldJointDef();
//		def.initialize(a, b, a.getPosition());
//		def.bodyA = a;
//		def.bodyB = b;
//		def.type = JointType.WeldJoint;
		
		//anchor the objects at their centers
		
		return world.createJoint(def);
	}
	
	public void removeJoint(Joint j)
	{
		world.destroyJoint(j);
	}
	
	/**
	 * check to see if there is any object obstructing a given space
	 * @param rect the area to check
	 * @return whether or not there is an object present
	 */
	public boolean checkSpace(Rectangle rect, GameObject except)
	{
		DetectObjectCallback cb = new DetectObjectCallback(except);
		world.QueryAABB(cb, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
		return cb.detected();
	}
	
	public GameObject getTargetableObjectAtPoint(Vector2 point)
	{
		//create rectangle centered at pixel, with an area of 1x1 tile
		Rectangle rect = new Rectangle(point.x - 0.5f,
				                       point.y - 0.5f,
				                       2f,
				                       2f);

		FindTargetableObjectAtPointCallback cb = new FindTargetableObjectAtPointCallback(null, point);
		
		world.QueryAABB(cb, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
		return cb.detected();
	}

}