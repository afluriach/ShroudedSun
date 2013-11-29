package com.pezventure.physics;

import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.objects.GameObject;

public class Physics
{
	public static final int VELOCITY_ITERATIONS = 8;
	public static final int POSITION_ITERATIONS = 3;
	
	public static final float DEFAULT_MASS = 1.0f;
	public static final float GRAVITY = 9.8f;
	
	public static final short enemyCategory = 1;
	public static final short enemyBulletCategory = 2;
	public static final short playerCategory = 4;
	public static final short playerBulletCategory = 8;
	public static final short floorObjectCategory = 16;
	public static final short aboveFloorObjectCategory = 32;
	public static final short playerShieldCategory = 64;
	
	public static Map<String, Filter> collisionFilters;
	
	public static void loadFilters()
	{
		collisionFilters = new TreeMap<String, Filter>();
		
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
		
	}
	
	public void removeBody(Body b)
	{
		world.destroyBody(b);
	}
	
	public void clear()
	{
		Gdx.app.log(Game.TAG, "clearing physics");
		world.dispose();
		world = new World(new Vector2(0,0), true);
		world.setContactListener(contactHandler);
		Gdx.app.log(Game.TAG,  "new world");
	}
	
	public void update()
	{
		//should be handled by contact handler instead
//		handleCollisions();
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
			
//			Gdx.app.log(Game.TAG, "collision: " + objA.toString() + objB.toString());
		}
	}
	
	private boolean contactInvolvesObject(Contact c, GameObject go)
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
	
	public GameObject feeler(Vector2 startingPos, float angleDeg, float distance, Class<?> targetCls)
	{
		Vector2 feeler = Util.ray(angleDeg, distance);
		Vector2 endPos = startingPos.cpy().add(feeler);
		
		FeelerRaycastCallback cb = new FeelerRaycastCallback(targetCls);
		world.rayCast(cb, startingPos, endPos);
		
		return cb.getResult();
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
	
	public Body addRectBody(Rectangle rect, GameObject ref, BodyType type)
	{
		return addRectBody(rect.getCenter(new Vector2()), rect.height, rect.width, type, ref);
	}

	public Body addRectBody(Rectangle rect, GameObject ref, BodyType type, float mass, boolean sensor)
	{
		return addRectBody(rect.getCenter(new Vector2()), rect.height, rect.width, type, ref, mass, sensor);
	}
	
	public Body addRectBody(Vector2 pos,float height, float width, BodyType type, GameObject ref)
	{
		return addRectBody(pos, height, width, type, ref, DEFAULT_MASS, false);
	}

	
	public Body addRectBody(Vector2 pos,
			                float height,
			                float width, 
			                BodyType type,
			                GameObject ref,
			                float mass,
			                boolean sensor)
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
		
//		Filter filter = new Filter();
		//set category bits here
		Filter filter = f.getFilterData();
		
		//default category 1, default mask -1 (all bits), default group 0
		
		shape.dispose();
		
		return b;
	}
	
	public Body addCircleBody(Vector2 pos, float radius, BodyType type, GameObject ref, float mass, boolean sensor)
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
}