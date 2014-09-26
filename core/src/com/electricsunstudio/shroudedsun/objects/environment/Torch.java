package com.electricsunstudio.shroudedsun.objects.environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.graphics.Animation;
import com.electricsunstudio.shroudedsun.graphics.Graphics;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.Element;
import com.electricsunstudio.shroudedsun.objects.Elemental;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.RenderLayer;
import com.electricsunstudio.shroudedsun.objects.projectile.Bullet;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;

public class Torch extends GameObject
{
//	private static final float SWITCH_SIZE = 1.0f;
//	private static final float BORDER_THICKNESS_PIXELS = 4;
	
	protected Texture texture;
	protected Animation flame;
	
	protected boolean lit = false;

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
		
		String torchSprite;
		String flameAnimation;
				
		if(to.prop.containsKey("color"))
		{
			torchSprite = to.prop.get("color", String.class) + "_torch";
			flameAnimation = to.prop.get("color", String.class) + "_flame32";
		}
		else
		{
			torchSprite = "torch";
			flameAnimation = "flame32";
		}
		
		texture = Game.inst.spriteLoader.getTexture(torchSprite);
		flame = Game.inst.spriteLoader.loadAnimation(flameAnimation, 0.125f, PrimaryDirection.up);
	
		if(to.prop.containsKey("light_time"))
		{
			lightTime = to.prop.get("light_time", Float.class);
		}
		else
		{
			lightTime = 0f;
		}
		
		lit = to.prop.containsKey("lit");
		
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
	
	public void setLit(boolean lit)
	{
		this.lit = lit;
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
