package com.pezventure.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.pezventure.Game;
import com.pezventure.map.MapUtil;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.Physics;

public abstract class GameObject 
{
	private static final float collisionBounceMargin = 0.1f;
	
	private static Map<String, Class<?>> gameObjectTypes = new HashMap<String, Class<?>>();
	private static boolean objectTypesInitialized = false;
	
	public static void addClass(String str, Class cls)
	{
		gameObjectTypes.put(str,  cls);
	}
	
	public static void addTypes()
	{
		addClass("wall", Wall.class);
		addClass("door", Door.class);
		addClass("block", Block.class);
		addClass("switch", FloorSwitch.class);
		addClass("floor_switch", FloorSwitch.class);
		addClass("invisible_switch", InvisibleFloorSwitch.class);
		addClass("invisible_floor_switch", InvisibleFloorSwitch.class);
		addClass("blue_enemy", BlueEnemy.class);
		addClass("jar", Jar.class);
		addClass("sign", Sign.class);
		addClass("facer", Facer.class);
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
	Body physicsBody;
	boolean collisionThisFrame = false;	
	
	String name;
	boolean expired = false;
	RenderLayer renderLayer = RenderLayer.above_floor;
	
	public GameObject(TilespaceRectMapObject mo)
	{
//		physicsBody = Physics.inst().addRectBody(mo.rect, this);
		
		name = mo.name;
		
//		Gdx.app.log(Game.TAG, String.format("GO name: %s, type: %s pos: %f,%f size: %f,%f", name, getClass().toString(), pos.x, pos.y, width, height));

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
	
	public static boolean allExpired(List<GameObject> list)
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
	
	/**
	 * undo displacement from this frame, intended to move GO back from collision
	 * add an extra margin to the reverse to make sure game object is no longer
	 * colliding
	 */
	public void undoFrameVel()
	{
		//direction of movement
		Vector2 dir = getVel().nor();		
		Vector2 dispThisFrame = getVel().scl(Game.SECONDS_PER_FRAME);
		Vector2 pos = getCenterPos();
		Vector2 bounceDisp = dir.cpy().scl(-collisionBounceMargin);
		
		Vector2 newpos = pos.cpy();

		newpos.sub(dispThisFrame);
		newpos.add(bounceDisp);
		
		
//		Gdx.app.log(Game.TAG, String.format("class: %s; disp: %f,%f; oldpos: %f,%f, newpos: %f,%f",
//                getClass().toString(), dispThisFrame.x, dispThisFrame.y, pos.x, pos.y, newpos.x, newpos.y));

		
		physicsBody.setTransform(newpos, physicsBody.getAngle());
	}

	public void setPos(Vector2 pos)
	{
		physicsBody.setTransform(pos, physicsBody.getAngle());
//	}
	}

//	/**
//	 * Push away the other GameObject so that it is no longer colliding. This
//	 * works for rectangular physicsbodies (hitboxes) for now. 
//	 * @param other the Game Object colliding with this
//	 */
//	public void pushCollider(GameObject other)
//	{
//		//physics parameters should be tuned so that tunnelling isn't possible, 
//		//but bias towards up/right in case coords equal
//		Vector2 thisPos = getCenterPos();
//		Vector2 otherPos = other.getCenterPos();
//		Vector2 otherNewPos = new Vector2(otherPos.x, otherPos.y);
//		
//		if(otherPos.x >= thisPos.x)
//		{
//			otherNewPos.x = thisPos.x + hitboxWidth/2 + other.hitboxWidth/2 + collisionBounceMargin;
//		}
//		else if(otherPos.x < thisPos.x)
//		{
//			otherNewPos.x = thisPos.x - hitboxWidth/2 - other.hitboxWidth/2 - collisionBounceMargin;
//		}
//		
//		if(otherPos.y >= thisPos.y)
//		{
//			otherNewPos.y = thisPos.y + hitboxHeight/2 + other.hitboxHeight/2 + collisionBounceMargin;
//		}
//		else if(otherPos.y < thisPos.y)
//		{
//			otherNewPos.y = thisPos.y - hitboxHeight/2 - other.hitboxHeight/2 - collisionBounceMargin;
//		}
//		
//		other.setPos(otherNewPos);
//	}
	
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
	
	public abstract void update();
	public abstract void render(SpriteBatch sb);	
	public abstract void handleContact(GameObject other);
	public abstract void handleEndContact(GameObject other);
	abstract void onExpire();
}
