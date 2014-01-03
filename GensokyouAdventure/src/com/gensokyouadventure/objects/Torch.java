package com.gensokyouadventure.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.graphics.Animation;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.physics.PrimaryDirection;

public class Torch extends GameObject
{
//	private static final float SWITCH_SIZE = 1.0f;
//	private static final float BORDER_THICKNESS_PIXELS = 4;
	
	private Texture texture;
	private Animation flame;
	
	private boolean lit = false;

	/**
	 * if 0, the torch stays lit indefinitely. if not, it will go out after the specified time
	 */
	private float lightTime;
	private float lightTimeRemaining;
	
	//adjusted so that the base of the flame sits in the torch properly. 
	private Vector2 flamePos;
		
	public Torch(TilespaceRectMapObject to)
	{
		super(to);
		
		texture = Game.inst.spriteLoader.getTexture("torch");
		flame = Game.inst.spriteLoader.loadAnimation("flame32", 0.125f, PrimaryDirection.up);
		
		if(to.prop.containsKey("light_time"))
		{
			lightTime = to.prop.get("light_time", Float.class);
		}
		else
		{
			lightTime = 0f;
		}
		
		renderLayer = RenderLayer.groundLevel;
				
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, BodyType.StaticBody, 1, false, "environmental_floor");
	}

	@Override
	public void render(SpriteBatch sb)
	{
		Graphics.drawTexture(texture, getCenterPos(), sb);

		if(lit)
		{
			flame.render(flamePos, sb);
		}
	}

	@Override
	public void update()
	{
		if(lit && lightTime != 0)
		{
			lightTimeRemaining -= Game.SECONDS_PER_FRAME;
			
			if(lightTimeRemaining <= 0f)
				lit = false;
		}
		
		flame.advance(Game.SECONDS_PER_FRAME);
	}

	@Override
	public void handleContact(GameObject other)
	{
		if(other instanceof Elemental)
		{
			if( ((Elemental) other).getElement() == Element.fire)
			{
				lit = true;
				lightTimeRemaining = lightTime;
				
				if(other instanceof Bullet)
					other.expire();
			}
			else if( ((Elemental) other).getElement() == Element.ice)
			{
				lit = false;
				
				if(other instanceof Bullet)
					other.expire();
			}

		}
	}

	@Override
	public void onExpire() {
		//no-op
	}
	
	public boolean isActivated()
	{
		return lit;
	}

	@Override
	public void handleEndContact(GameObject other)
	{
	}

	@Override
	public void init() {
		flamePos = getCenterPos().add(new Vector2(0f, 0.2f));

	}
	
	public void light()
	{
		lit = true;
	}
	
	public void unlight()
	{
		lit = false;
	}

}
