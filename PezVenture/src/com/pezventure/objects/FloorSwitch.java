package com.pezventure.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	private  Texture inactiveTexture;
	private Texture activeTexture;
	
	//this kind of switch is activated when a particular kind of GameObject is placed above it
	//if not a sticky switch, track the activating object in case it moves off the switch
	private boolean activated = false;
	private boolean sticky;
	private GameObject activatingObject;
	
	//by default, switches activate if the player steps on them
	private Class<?> activatingClass  = Player.class;
	//distance from the center of the switch required to activate it
	private float activationMargin = 0.25f;
	
	public FloorSwitch(TilespaceRectMapObject to)
	{
		super(to);
		
		inactiveTexture = Game.inst.spriteLoader.getTexture("switch_inactive");
		activeTexture = Game.inst.spriteLoader.getTexture("switch_active");
		
		sticky = Boolean.parseBoolean(to.prop.get("sticky", String.class));

		if(to.prop.containsKey("activating_class"))
		{
			activatingClass = GameObject.getObjectClass(to.prop.get("actitvating_class", String.class));
		}
		if(to.prop.containsKey("activation_margin"))
		{
			activationMargin = Float.parseFloat(to.prop.get("activation_margin", String.class));
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
		if(!sticky && activated)
		{
			if(activatingObject.getCenterPos().sub(getCenterPos()).len2() >= activationMargin*activationMargin)
			{
				activated = false;
			}
		}
	}

	@Override
	public void handleCollision(GameObject other)
	{
		if(activatingClass.isInstance(other) &&
		   other.getCenterPos().sub(getCenterPos()).len2() <= activationMargin*activationMargin)
		{
			activated = true;
			activatingObject = other;
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

}
