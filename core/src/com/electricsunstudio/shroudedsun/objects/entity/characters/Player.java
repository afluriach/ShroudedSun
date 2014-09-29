package com.electricsunstudio.shroudedsun.objects.entity.characters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.Util;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.RadarSensor;
import com.electricsunstudio.shroudedsun.objects.entity.Entity;
import com.electricsunstudio.shroudedsun.objects.entity.NPC;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Enemy;
import com.electricsunstudio.shroudedsun.objects.environment.Door;
import com.electricsunstudio.shroudedsun.objects.environment.Jar;
import com.electricsunstudio.shroudedsun.objects.environment.SavePoint;
import com.electricsunstudio.shroudedsun.objects.environment.Sign;
import com.electricsunstudio.shroudedsun.objects.environment.TreasureChest;
import com.electricsunstudio.shroudedsun.objects.interaction.GrabItem;
import com.electricsunstudio.shroudedsun.objects.interaction.Grabbable;
import com.electricsunstudio.shroudedsun.objects.interaction.ItemInteraction;
import com.electricsunstudio.shroudedsun.objects.interaction.OpenChest;
import com.electricsunstudio.shroudedsun.objects.interaction.OpenDoor;
import com.electricsunstudio.shroudedsun.objects.interaction.Read;
import com.electricsunstudio.shroudedsun.objects.interaction.Save;
import com.electricsunstudio.shroudedsun.objects.interaction.Talk;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;
import com.electricsunstudio.shroudedsun.objects.Level2Spirit;


public abstract class Player extends Entity {
	public static final float SPEED = 3.0f;
	private static final float invulerabilityLength = 1.0f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	public static final float shotInitDist = 0.5f;
	private static final float itemInteractDistance = 0.5f;
	// where to hold am item relative to the player's center.
	private static final Vector2 itemHoldPos = new Vector2(0f, 0.2f);

	// map gameobject type to the kind of interaction that can be performed with
	// in.
	private HashMap<Class<? extends GameObject>, ItemInteraction> interactMap;

//	int hp = MAX_HP;
//	int mp = MAX_MP;
	float invulnerableTimeRemaining = 0f;

	// interaction logic, including interacting with obejcts in the environment
	// and carrying objects
	public GameObject holdingItem;
	public String interactMessage = "";
	private GameObject interactibleObject;
	private ItemInteraction interaction;

	// flag that determines if the player wants to shoot, set based on the
	// controls.
	boolean interact = false;
	
	public RadarSensor targetableSensor;
	float targetRadius = 12f;
	float targetingFOV = 90f;
	
	private void initInteractMap() {
		interactMap = new HashMap<Class<? extends GameObject>, ItemInteraction>();

		interactMap.put(Sign.class, new Read());
		interactMap.put(NPC.class, new Talk());
		interactMap.put(Jar.class, new GrabItem());
		interactMap.put(Level2Spirit.class, new GrabItem());
		interactMap.put(Door.class, new OpenDoor());
		interactMap.put(SavePoint.class, new Save());
		interactMap.put(TreasureChest.class, new OpenChest());
	}

	public Player(Vector2 pos, PrimaryDirection startingDir, String character) {
		super(pos, character, startingDir.getAngle8Dir(), "player", "player", false);
		
		LinkedList<Class<?>> targetClasses = new LinkedList<Class<?>>();
		targetClasses.add(Enemy.class);
		targetClasses.add(NPC.class);
		targetableSensor = new RadarSensor(getCenterPos(), targetRadius, targetClasses, "targeting_sensor");

		initInteractMap();
	}

	@Override
	public void update() {
		invulnerableTimeRemaining -= Game.SECONDS_PER_FRAME;
		if (invulnerableTimeRemaining < 0) {
			invulnerableTimeRemaining = 0;
		}
		
		targetableSensor.setPos(getCenterPos());

		checkInteract();

		if (interact) {
			interact();
			interact = false;
		}

		if (holdingItem != null) {
			holdingItem.setPos(getCenterPos().add(itemHoldPos));
            
            //drop item if it is no longer holdable
            if(!((Grabbable)holdingItem).canGrab())
                dropInPlace();
    	}

		super.update();
	}

	@Override
	public void handleContact(GameObject other) {
		// if(Util.arrayContains(grabable, other.getClass()))
		// grabableTouching.add(other);
	}

	@Override
	public void onExpire() {
		// TODO Auto-generated method stub

	}

	public void hit(int damage) {
		if (invulnerableTimeRemaining == 0) {
			Game.inst.playerHP -= damage;
			invulnerableTimeRemaining = invulerabilityLength;
			enableFlicker(invulerabilityLength, invulerabilityFlickerInterval);
		}
	}
	
	public boolean isInvulnerable()
	{
		return invulnerableTimeRemaining == 0;
	}

	/**
	 * ignore invulerability effect
	 * 
	 * @param hp
	 */
	public void directDamage(int hp) {
		Game.inst.playerHP -= hp;

	}

	@Override
	public void handleEndContact(GameObject other) {
		// grabableTouching.remove(other);
	}

	void checkItemClass(GameObject obj)
	{
		for(Class<? extends GameObject> cls : interactMap.keySet())
		{
			if(cls.isInstance(obj))
			{
				ItemInteraction action = interactMap.get(cls);
				
				if(action.canInteract(obj, this))
				{
					setItemInteraction(obj, action);
					return;
				}
			}
		}
		
		blankInteraction();
	}
	
	void setItemInteraction(GameObject obj, ItemInteraction action)
	{
		interaction = action;
		interactMessage = action.interactMessage();
		interactibleObject = obj;
	}
	
	void blankInteraction()
	{
		interactMessage = "";
		interaction = null;
		interactibleObject = null;
	}
	
	/**
	 * checks for a smart object (interactible) and set the interact message.
	 * save the object and interaction if applicable.
	 */
	public void checkInteract()
	{
		// if the player is holding an item, the drop action takes precedence.
		if (holdingItem != null)
		{
			// if there is no room to drop item, do not display message
			interactMessage = canDrop() ? "Drop" : "";
		}
		else if(Game.inst.target != null && Game.inst.target instanceof NPC)
		{
			//else if targeting an NPC, the talk action takes precedence
			interactMessage = "Talk";
		}
		else
		{
			GameObject obj = Game.inst.physics.closestObjectFeeler(getCenterPos(),
					getFacingAngle(), HIT_CIRCLE_RADIUS + itemInteractDistance,
					GameObject.class);

			checkItemClass(obj);
		}
	}

	/**
	 * assumes checkInteract was already called this frame so the interaction
	 * data is up to date.
	 */
	public void interact() {
		if (holdingItem != null && canDrop()) {
			drop();
		}
		else if(Game.inst.target != null && Game.inst.target instanceof NPC)
		{
			interactMap.get(NPC.class).interact(Game.inst.target, this);
		}
		else if (interactibleObject != null) {
			interaction.interact(interactibleObject, this);
		}
	}
    
	// only drop if there is room in front of the player to place jar.
	private boolean canDrop() {
		// the center position where the item would be placed
		Vector2 holdDisp = Util.ray(getFacingAngle(), itemInteractDistance + HIT_CIRCLE_RADIUS);

		// the AABB where the jar would be placed
		Rectangle rect = new Rectangle(holdingItem.getAABB());
		rect.setCenter(getCenterPos().add(holdDisp));

		return !Game.inst.physics.checkSpace(rect, holdingItem);
	}

	private void drop() {
		Vector2 holdDisp = Util.ray(getFacingAngle(), itemInteractDistance + HIT_CIRCLE_RADIUS);
		holdingItem.setPos(getCenterPos().add(holdDisp));
		((Grabbable) holdingItem).onDrop();

		holdingItem = null;
	}
    
    private void dropInPlace()
    {
        ((Grabbable)holdingItem).onDrop();
        holdingItem = null;
    }

	public void setInteract() {
		interact = true;
	}

	@Override
	public void init() {
	}
	
	public List<GameObject> getTargetableObjects()
	{
		return targetableSensor.getDetectedObjects(getFacingAngle(), targetingFOV);
	}
	
	//get target that is closest to the player's center FOV
	public GameObject getPrimaryTarget()
	{
		float bestDot = 0f;
		GameObject bestObj = null;
		for(GameObject go : getTargetableObjects())
		{
			Vector2 disp = go.getCenterPos().sub(getCenterPos());
			float dot = disp.nor().dot(Util.ray(getFacingAngle(), 1f));

			if(bestObj == null)
			{
				bestObj = go;
				bestDot = dot;
			}
			else if(dot > bestDot)
			{
				bestObj = go;
				bestDot = dot;
			}
		}

		return bestObj;
	}
}
