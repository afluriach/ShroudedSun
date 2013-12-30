package com.pezventure.objects;

import java.util.HashMap;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.physics.PrimaryDirection;

public class Player extends Entity
{
	public static final float SPEED = 3.0f;
	private static final int MAX_HP = 10;
	private static final float invulerabilityLength = 1.0f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	private static final float fireInterval = 0.7f;
	private static final float shotInitDist = 0.5f;
	private static final float itemInteractDistance = 0.5f;
	
	//map gameobject type to the kind of interaction that can be performed with in.
	private HashMap<Class<? extends GameObject>, ItemInteraction> interactMap;
	
	int hp = MAX_HP;
	float invulnerableTimeRemaining = 0f;
	float fireTimeRemaining = 0f;
	Shield shieldObj;
	
	//interaction logic, including interacting with obejcts in the environment and carrying objects
	public GameObject holdingItem;
	Joint itemJoint;
	public String interactMessage = "";
	private GameObject interactibleObject;
	private ItemInteraction interaction;
	
	//flag that determines if the player wants to shoot, set based on the controls.
	boolean shoot = false;
	boolean interact = false;
	public boolean shield = false;
	
	private void initInteractMap()
	{
		interactMap = new HashMap<Class<? extends GameObject>, ItemInteraction>();
		
		interactMap.put(Sign.class, new Read());
		interactMap.put(NPC.class, new Talk());
		interactMap.put(Jar.class, new GrabItem());
		
	}
	
	public Player(Vector2 pos, PrimaryDirection startingDir)
	{
		super(pos, "cirno", startingDir.getAngle8Dir(), "player", "player");
		
		shieldObj = new Shield(getCenterPos());
		//physicsBody = Physics.inst().addRectBody(pos, PLAYER_SIZE, PLAYER_SIZE, BodyType.DynamicBody, this, MASS, false);
		Game.inst.gameObjectSystem.addObject(shieldObj);
		
		initInteractMap();
	}
	
	public void update()
	{		
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
		
		 //can't shoot or use shieldwhile holding an item.
		if(holdingItem != null)
		{
			shoot = false;
			shield = false;
		}
		
		//cooldown time between shots
		if(shoot && fireTimeRemaining <= 0)
		{
			//bullet inital pos shouldnt matter. it's position will be updated by the 
			//shoot method
			shoot(new PlayerIceBullet(getCenterPos(), getDir()), shotInitDist);
			shoot = false;
			fireTimeRemaining = fireInterval;
		}
		
		
		//may be generalized to interaction button
		checkInteract();		

		if(interact)
		{
			interact();
			interact = false;
		}	
		
		if(holdingItem != null)
		{
			Vector2 holdDisp = Util.get8DirUnit(getDir()).scl(itemInteractDistance+HIT_CIRCLE_RADIUS);
			holdingItem.setPos(getCenterPos().add(holdDisp));
		}
		
		shieldObj.setActive(shield);
		shieldObj.setPos(getCenterPos());
		
		super.update();
	}
	
	@Override
	public void handleContact(GameObject other)
	{
//		if(Util.arrayContains(grabable, other.getClass()))
//			grabableTouching.add(other);
	}

	@Override
	public void onExpire()
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
	
	public void setShield()
	{
		shield = true;
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
	 * checks for a smart object (interactible) and set the interact message. save the object and interaction if applicable.
	 */
	public void checkInteract()
	{
		//if the player is holding an item, the drop action takes precedence.
		if(holdingItem != null)
		{
			interactMessage = "Drop";
			return;
		}
		
		GameObject obj = Game.inst.physics.closestObjectFeeler(getCenterPos(), getDir()*45f, HIT_CIRCLE_RADIUS+itemInteractDistance, GameObject.class);
		
		if(obj != null && interactMap.containsKey(obj.getClass()))
		{
			interaction = interactMap.get(obj.getClass());
			if(interaction.canInteract(obj, this))
			{
				interactMessage = interaction.interactMessage();
				interactibleObject = obj;
			}
		}
		else
		{
			interactMessage = "";
			interaction = null;
			interactibleObject = null;
		}
	}
	
	/**
	 * assumes checkInteract was already called this frame so the interaction data is up to date. 
	 */
	public void interact()
	{
		if(holdingItem != null)
		{
			drop();
		}
		else if(interactibleObject != null)
		{
			interaction.interact(interactibleObject, this);
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
	
	@Override
	public void render(SpriteBatch sb)
	{
		if(animation != null && showingSprite)
		{
			animation.render(sb, getCenterPos());
		}
	}
	
	public void setInteract()
	{
		interact = true;
	}

	@Override
	public void init() {
	}


}
