package com.pezventure.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pezventure.Game;
import com.pezventure.objects.GameObject;

public class Physics
{
	public static final int VELOCITY_ITERATIONS = 8;
	public static final int POSITION_ITERATIONS = 3;
	
	public static final float DEFAULT_MASS = 1.0f;
	public static final float GRAVITY = 9.8f;
	
	World world;
	
	Box2DDebugRenderer box2dRenderer = new Box2DDebugRenderer();
		
	public Physics()
	{
		//no gravity, allow sleeping objects
		world = new World(new Vector2(0,0), true);
		box2dRenderer.setDrawAABBs(true);
		box2dRenderer.setDrawBodies(true);
		box2dRenderer.setDrawInactiveBodies(true);
	}
	
	public void update()
	{
		handleCollisions();
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
			
			objA.handleCollision(objB);
			objB.handleCollision(objA);
			
//			Gdx.app.log(Game.TAG, "collision: " + objA.toString() + objB.toString());
		}
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

	
	public Body addRectBody(Vector2 pos,float height, float width, BodyType type, GameObject ref, float mass, boolean sensor)
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
		
		return b;
	}
	
	public void debugRender(Matrix4 combined)
	{
		box2dRenderer.render(world, combined.cpy().scale(32, 32, 1));
	}
}