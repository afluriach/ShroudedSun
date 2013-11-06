package com.pezventure.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.graphics.EntityAnimation;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.Physics;
import com.pezventure.physics.PrimaryDirection;

public abstract class Entity extends GameObject
{
	//cycle through animation (show a step), every time the entity covers distance equal to one step
	static final float stepLength = 0.25f;
	static final float MASS = 50.0f;
	static final float HIT_CIRCLE_RADIUS = 0.35f;
	
	//entities may flicker to show temporary invulnerbility after being attacked or when about to expire. 
	boolean isFlickering = false;
	boolean showingSprite = true;
	float flickertotalTimeRemaining = 0;
	float flickerIntervalLength = 0;
	float flickerIntervalTimeRemaining = 0;
	
	float stepDistAccumulated=0;
	
	EntityAnimation animation;
	
	PrimaryDirection crntDir;
	PrimaryDirection desiredDir;
	float speed;
		
	public Entity(TilespaceRectMapObject to, float speed, EntityAnimation animation)
	{
		super(to);
		this.speed = speed;
		this.animation = animation;
		//physicsBody = Physics.inst().addRectBody(to.rect, this, BodyType.DynamicBody, MASS, false);
		physicsBody = Game.inst.physics.addCircleBody(to.rect.getCenter(new Vector2()), HIT_CIRCLE_RADIUS, BodyType.DynamicBody, this, MASS, false);
	}
	
	public Entity(Vector2 pos, float height, float width, float speed, EntityAnimation animation, String name)
	{
		super(pos, height, width, name);
		this.speed = speed;
		this.animation = animation;
		physicsBody = Game.inst.physics.addCircleBody(pos, HIT_CIRCLE_RADIUS, BodyType.DynamicBody, this, MASS, false);
	}
	
	/**
	 * Supports only primary (4-dir), single magnitude movement. 
	 * @param dir direction to move, or null for no movement
	 */
	public void setVel(PrimaryDirection dir)
	{
//		Gdx.app.log(Game.TAG, "dir set: " + (dir == null ? "null" : dir.toString()));
		if(dir == null)
		{
			animation.resetAnimation();
			setVel(Vector2.Zero);
			stepDistAccumulated = 0;
		}
		else if(dir != crntDir)
		{
			animation.setDirection(dir);
			animation.resetAnimation();
			setVel(dir.getUnitVector().scl(speed));
		}
		crntDir = dir;
	}
	
	public void setDesiredDir(PrimaryDirection desired)
	{
		desiredDir = desired;
	}
	
	public void enableFlicker(float flickerTime, float flickerInterval)
	{
		this.isFlickering= true;
		this.flickerIntervalLength = flickerInterval;
		this.flickertotalTimeRemaining = flickerTime;
		this.flickerIntervalTimeRemaining = flickerInterval;
	}
		
	public void render(SpriteBatch sb)
	{
		if(animation != null && showingSprite)
		{
			animation.render(sb, getCenterPos());
		}
	}
	
	public void update()
	{
		
		if(isFlickering)
		{
			if(flickerIntervalTimeRemaining <=0 )
			{
				showingSprite = !showingSprite;
				flickerIntervalTimeRemaining = flickerIntervalLength;
			}

			if(flickertotalTimeRemaining <=0)
			{
				isFlickering = false;
				showingSprite = true;
			}
			
			flickerIntervalTimeRemaining -= Game.SECONDS_PER_FRAME;
			flickertotalTimeRemaining -= Game.SECONDS_PER_FRAME;
			
			
		}
		
		setVel(desiredDir);
		
		stepDistAccumulated += getVel().len()*Game.SECONDS_PER_FRAME;
		if(stepDistAccumulated >= stepLength)
		{
			animation.incrementFrame();
			stepDistAccumulated -= stepLength;
		}
//		Gdx.app.log(Game.TAG, String.format("step dist acc: %f, vel mag: %f", stepDistAccumulated, vel.len()));
	}

}
