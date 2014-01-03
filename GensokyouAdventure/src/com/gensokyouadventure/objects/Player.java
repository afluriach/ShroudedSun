package com.gensokyouadventure.objects;

import java.util.HashMap;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.physics.PrimaryDirection;

public class Player extends Entity {
	public static final float SPEED = 3.0f;
	private static final int MAX_HP = 10;
	private static final int MAX_MP = 10;
	private static final float invulerabilityLength = 1.0f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	public static final float shotInitDist = 0.5f;
	private static final float itemInteractDistance = 0.5f;
	// where to hold am item relative to the player's center.
	private static final Vector2 itemHoldPos = new Vector2(0f, 0.2f);

	// map gameobject type to the kind of interaction that can be performed with
	// in.
	private HashMap<Class<? extends GameObject>, ItemInteraction> interactMap;

	int hp = MAX_HP;
	int mp = MAX_MP;
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

	private void initInteractMap() {
		interactMap = new HashMap<Class<? extends GameObject>, ItemInteraction>();

		interactMap.put(Sign.class, new Read());
		interactMap.put(NPC.class, new Talk());
		interactMap.put(Jar.class, new GrabItem());
		interactMap.put(Door.class, new OpenDoor());
	}

	public Player(Vector2 pos, PrimaryDirection startingDir) {
		super(pos, "cirno", startingDir.getAngle8Dir(), "player", "player", false);

		initInteractMap();
	}

	public void update() {
		invulnerableTimeRemaining -= Game.SECONDS_PER_FRAME;
		if (invulnerableTimeRemaining < 0) {
			invulnerableTimeRemaining = 0;
		}

		checkInteract();

		if (interact) {
			interact();
			interact = false;
		}

		if (holdingItem != null) {
			holdingItem.setPos(getCenterPos().add(itemHoldPos));
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

	public int getHP() {
		return hp;
	}

	public int getMaxHP() {
		return MAX_HP;
	}

	public int getMP() {
		return mp;
	}

	public void setHP(int hp) {
		this.hp = hp;
	}

	public void setMP(int mp) {
		this.mp = mp;
	}

	public int getMaxMP() {
		return MAX_MP;
	}

	public void useMP(int amount) {
		mp -= amount;
	}

	public void heal(int hp) {
		this.hp += hp;
		if (this.hp > MAX_HP)
			this.hp = MAX_HP;
	}

	public void hit(int damage) {
		if (invulnerableTimeRemaining == 0) {
			hp -= damage;
			invulnerableTimeRemaining = invulerabilityLength;
			enableFlicker(invulerabilityLength, invulerabilityFlickerInterval);
		}
	}

	/**
	 * ignore invulerability effect
	 * 
	 * @param hp
	 */
	public void directDamage(int hp) {
		this.hp -= hp;

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
			return;
		}

		GameObject obj = Game.inst.physics.closestObjectFeeler(getCenterPos(),
				getDir() * 45f, HIT_CIRCLE_RADIUS + itemInteractDistance,
				GameObject.class);

		checkItemClass(obj);
	}

	/**
	 * assumes checkInteract was already called this frame so the interaction
	 * data is up to date.
	 */
	public void interact() {
		if (holdingItem != null && canDrop()) {
			drop();
		} else if (interactibleObject != null) {
			interaction.interact(interactibleObject, this);
		}
	}

	// only drop if there is room in front of the player to place jar.
	private boolean canDrop() {
		// the center position where the item would be placed
		Vector2 holdDisp = Util.get8DirUnit(getDir()).scl(
				itemInteractDistance + HIT_CIRCLE_RADIUS);

		// the AABB where the jar would be placed
		Rectangle rect = new Rectangle(holdingItem.getAABB());
		rect.setCenter(getCenterPos().add(holdDisp));

		return !Game.inst.physics.checkSpace(rect, holdingItem);
	}

	private void drop() {
		Vector2 holdDisp = Util.get8DirUnit(getDir()).scl(
				itemInteractDistance + HIT_CIRCLE_RADIUS);
		holdingItem.setPos(getCenterPos().add(holdDisp));
		((Grabbable) holdingItem).onDrop();

		holdingItem = null;
	}

	@Override
	public void render(SpriteBatch sb) {
		if (animation != null && showingSprite) {
			animation.render(sb, getCenterPos());
		}
	}

	public void setInteract() {
		interact = true;
	}

	@Override
	public void init() {
	}

}
