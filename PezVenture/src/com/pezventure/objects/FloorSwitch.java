package com.pezventure.objects;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.graphics.Graphics;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.Physics;

public class FloorSwitch extends GameObject
{
//	private static final float SWITCH_SIZE = 1.0f;
//	private static final float BORDER_THICKNESS_PIXELS = 4;
	
	private Texture inactiveTexture;
	private Texture activeTexture;
	
	private boolean activated = false;
	/**
	 * Does the switch stay activated unconditionally after it has been activated? If not, requires an appropriately
	 * placed activating object on top of it.
	 */
	private boolean sticky;
	
	/**
	 * The class of GameObject that can activate this switch. The activating class is the Player
	 * class by default.
	 */
	private Class<?> activatingClass  = Player.class;
	
	/**
	 * if set, then only a GameObject with this specific name can activate the switch
	 */
	private String activatingObjectName;
	
	/**
	 * maximum distance from center of switch to center of object in order to activate.
	 */
	private float activationRadius = 0.25f;
	
	/**
	 * Objects of the activating class that have collided with the switch. objects are added during handleContact
	 * and removed during handleEndContact. note: activation is based on activation radius and the relative position
	 * of the object and the switch.
	 */
	private List<GameObject> activatingObjectsOnSwitch = new LinkedList<GameObject>();
	
	public FloorSwitch(TilespaceRectMapObject to)
	{
		super(to);
		
		inactiveTexture = Game.inst.spriteLoader.getTexture("switch_inactive");
		activeTexture = Game.inst.spriteLoader.getTexture("switch_active");
		
		sticky = Boolean.parseBoolean(to.prop.get("sticky", String.class));

		if(to.prop.containsKey("activating_class"))
		{
			activatingClass = GameObject.getObjectClass(to.prop.get("activating_class", String.class));
		}
		if(to.prop.containsKey("activation_margin"))
		{
			activationRadius = Float.parseFloat(to.prop.get("activation_margin", String.class));
		}
		if(to.prop.containsKey("activating_name"))
		{
			activatingObjectName = to.prop.get("activating_name", String.class);
		}
		
		renderLayer = RenderLayer.floor;
				
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.DynamicBody, 1, true);
	}

	@Override
	public void render(SpriteBatch sb)
	{
		Graphics.drawTexture(activated ? activeTexture : inactiveTexture,
				             getCenterPos(), sb);
	}

	@Override
	public void update()
	{
		if(!activated || (activated && !sticky))
		{
			activated = activatedByObject();
		}
		
//		if(!sticky && activated)
//		{
//			if(activatingObject.getCenterPos().sub(getCenterPos()).len2() >= activationMargin*activationMargin)
//			{
//				activated = false;
//			}
//		}
	}

	@Override
	public void handleContact(GameObject other)
	{
		Gdx.app.log(Game.TAG, other.toString());
		
		//if an activating name is set, only check for objects with that
		//name, otherwise filter based on class
		
		if(activatingObjectName != null)
		{
			if(other.name.equals(activatingObjectName))
			{
				activatingObjectsOnSwitch.add(other);
			}
		}
		else if(activatingClass.isInstance(other))
		{
			Gdx.app.log(Game.TAG, other.getClass() + " on switch");
			activatingObjectsOnSwitch.add(other);
		}

	}

	@Override
	void onExpire() {
		//no-op
	}
	
	public boolean isActivated()
	{
		return activated;
	}

	@Override
	public void handleEndContact(GameObject other)
	{
		if(activatingObjectName != null && activatingObjectName.equals(other.name))
			activatingObjectsOnSwitch.remove(other);
		if(activatingClass.isInstance(other))
			activatingObjectsOnSwitch.remove(other);		
	}

	/**
	 * 
	 * @return whether any currently touching GameObject of the activating classs (in the list) activates the 
	 * switch by position.
	 */
	private boolean activatedByObject()
	{
		for(GameObject go : activatingObjectsOnSwitch)
			if(go.getCenterPos().sub(getCenterPos()).len2() < activationRadius*activationRadius)
				return true;
		return false;
	}
}
