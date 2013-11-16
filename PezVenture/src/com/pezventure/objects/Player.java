package com.pezventure.objects;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Joint;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.physics.Physics;
import com.pezventure.physics.PrimaryDirection;

public class Player extends Entity
{
	private static final float SPEED = 2.0f;
	private static final float PLAYER_SIZE = 0.75f;
	private static final int MAX_HP = 10;
	private static final float invulerabilityLength = 1.0f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	private static final float fireInterval = 0.7f;
	private static final float shotInitDist = 0.5f;
	private static final float grabAngle = 15f;
	private static final float grabDistance = 0.5f;
	
	/**
	 * types of game objects the player can grab.
	 */
	private Class[] grabable = {Jar.class};
	private LinkedList<GameObject> grabableTouching = new LinkedList<GameObject>();
	
	int hp = MAX_HP;
	float invulnerableTimeRemaining = 0f;
	float fireTimeRemaining = 0f;
	
	//grabbing logic
	public GameObject holdingItem;
	Joint itemJoint;
	//is there a grabbable object in front of the player
	public boolean canGrab = false;
	
	//flag that determines if the player wants to shoot, set based on the controls.
	boolean shoot = false;
	boolean grab = false;
	
	public Player(Vector2 pos)
	{
		super(pos,
			  PLAYER_SIZE,
			  PLAYER_SIZE,
			  SPEED,
			  Game.inst.spriteLoader.getSpriteAnimation("link_dark_hat", PrimaryDirection.up), "player");
		
		//physicsBody = Physics.inst().addRectBody(pos, PLAYER_SIZE, PLAYER_SIZE, BodyType.DynamicBody, this, MASS, false);
	}
	
	public void update()
	{
		super.update();
		
		invulnerableTimeRemaining -= Game.SECONDS_PER_FRAME;
		if(invulnerableTimeRemaining < 0)
		{
			invulnerableTimeRemaining = 0;
		}
		
		fireTimeRemaining -= Game.SECONDS_PER_FRAME;
		if(fireTimeRemaining < 0)
		{
			fireTimeRemaining = 0;
		}
		
		 //can't shoot while holding an item.
		if(holdingItem != null)
			shoot = false;
		
		//cooldown time between shots
		if(shoot && fireTimeRemaining <= 0)
		{
			//bullet inital pos shouldnt matter. it's position will be updated by the 
			//shoot method
			shoot(new PlayerBullet(getCenterPos(), facing), shotInitDist);
			shoot = false;
			fireTimeRemaining = fireInterval;
		}
		
		
		//may be generalized to interaction button
		
		checkGrabbable();

		if(grab)
		{
			if(holdingItem == null)
				grab();
			else
				drop();
			grab = false;
		}	
		
		if(holdingItem != null)
		{
			Vector2 holdDisp = facing.getUnitVector().scl(grabDistance+HIT_CIRCLE_RADIUS);
			holdingItem.setPos(getCenterPos().add(holdDisp));
		}
	}
	
	@Override
	public void handleContact(GameObject other)
	{
//		if(Util.arrayContains(grabable, other.getClass()))
//			grabableTouching.add(other);
	}

	@Override
	void onExpire()
	{
		// TODO Auto-generated method stub
		
	}

	public int getHP() {
		return hp;
	}

	public int getMaxHP() {
		return MAX_HP;
	}

	public void heal(int hp)
	{
		this.hp += hp;
		if(this.hp > MAX_HP)
			this.hp = MAX_HP;
	}

	public void hit(int damage)
	{
		if(invulnerableTimeRemaining == 0)
		{
			hp -= damage;
			invulnerableTimeRemaining = invulerabilityLength;
			enableFlicker(invulerabilityLength, invulerabilityFlickerInterval);
		}
	}
	
	/**
	 * ignore invulerability effect
	 * @param hp
	 */
	public void directDamage(int hp) 
	{
		this.hp -= hp;
		
	}
	
	public void setDesireToShoot()
	{
		if(fireTimeRemaining <= 0)
			shoot = true;
	}
	
	public void setGrab()
	{
		grab = true;
	}
	
	@Override
	public void handleEndContact(GameObject other)
	{
//		grabableTouching.remove(other);
	}
	
//	public boolean canGrabObject(GameObject go)
//	{
//		Vector2 disp = go.getCenterPos().sub(getCenterPos());
//		
//		return Math.abs(disp.angle() - facing.getAngle()) < grabAngle;
//	}
	
	/**
	 * checks for a grabbable object and sets the grabbable field
	 * @return is there is a grabbale object in front of the player
	 */
	public boolean checkGrabbable()
	{
		Grabbable target = (Grabbable) Game.inst.physics.feeler(getCenterPos(), facing.getAngle(), HIT_CIRCLE_RADIUS+grabDistance, Grabbable.class);
		canGrab = (target != null && target.canGrab());
		
		if(canGrab)
			System.out.println("can grab");
		
		return canGrab;
			
	}
	
	public void grab()
	{
		//find grabable item player that is in front of the player
		Grabbable target = (Grabbable) Game.inst.physics.feeler(getCenterPos(), facing.getAngle(), HIT_CIRCLE_RADIUS+grabDistance, Grabbable.class);
		GameObject go = (GameObject) target;
		
		Gdx.app.log(Game.TAG, "target: " + go);
		if(target != null  && target.canGrab())
		{
			holdingItem = (GameObject) target;
			//itemJoint = Game.inst.physics.joinBodies(physicsBody, go.physicsBody);
			target.onGrab();
		}
	}
	
	public void drop()
	{
		Gdx.app.log(Game.TAG, "player drop");
//		Game.inst.physics.removeJoint(itemJoint);
		((Grabbable)holdingItem).onDrop();
		holdingItem = null;
		itemJoint = null;
	}

}
