package com.electricsunstudio.shroudedsun.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.Game;

public class EntityAnimation8Dir
{
	//animation frame 0 represents with right foot forward, frame 2 is left, farme 1 mid-step
	static final int STANDING_FRAME = 1;
	static final float stepLength = 1f;
	static final String leftFoot = "footstep_high_soft";
	static final String rightFoot = "footstep_low_soft";
	
	private EntitySpriteSet8Dir spriteSet;
	
	/**
	 * angle in degrees divided by 45.
	 */
	private int crntDirection;
	private int crntFrame = STANDING_FRAME;
	private boolean leftStep = true;
	private boolean walking = false;
	
	private boolean leftSound = false;
	private boolean rightSound = false;
	
	//cycle through animation (show a step), every time the entity covers distance equal to one step.
	float stepDistAccumulated=0;
		
	public EntityAnimation8Dir(EntitySpriteSet8Dir s, int startingDir)
	{
		crntDirection = startingDir;
		if(s==null)
			throw new NullPointerException("null entity sprite set given");
		spriteSet = s;
	}
	
	public int getFrame()
	{
		return crntFrame;
	}
	
	//called every time the entity has accumulate a half-step distance.
	public void incrementFrame()
	{
		//if entity was standing still, take a half step, then switch to walking animation
		if(!walking)
		{
			crntFrame = 2;
			leftStep = false;
			walking = true;
			leftSound = true;
		}
		else
		{
			if(leftStep)
			{
				++crntFrame;	
				if(crntFrame == 2) leftStep = false;
				leftSound = true;
			}
			else
			{
				--crntFrame;
				if(crntFrame == 0) leftStep = true;
				rightSound = true;
			}					
		}
	}
	
	public void render(SpriteBatch batch, Vector2 centerPos)
	{
		int centerPixX = (int) (centerPos.x*Game.PIXELS_PER_TILE);
		int centerPixY = (int) (centerPos.y*Game.PIXELS_PER_TILE);
		//lower left corner
		int x = centerPixX- spriteSet.spriteSize/2;
		int y = centerPixY - spriteSet.spriteSize/2;
		
		boolean flip = crntDirection >= 3 && crntDirection <= 5;
		TextureRegion region = spriteSet.sprites[getSpriteRow()][crntFrame];
		
		if(flip) region.flip(true, false);	
		batch.draw(region, x, y);
		if(flip) region.flip(true, false);
	}
	
	public void checkSound(Vector2 pos)
	{
		if(leftSound)
			Game.inst.soundLoader.playSound(leftFoot, pos);
		else if(rightSound)
			Game.inst.soundLoader.playSound(rightFoot, pos);
		
		leftSound = false;
		rightSound = false;
			
	}
	
	public int getSpriteRow()
	{
		switch(crntDirection)
		{
		case 0: case 4: return 2;
		case 1: case 3: return 1;
		case 2: 		return 0;
		case 5: case 7: return 3;
		case 6: 		return 4;
		default:		throw new IllegalStateException();
		}
		
	}
	
	public void setDirection(int newDir)
	{
		crntDirection = newDir;
		//when changing direction, stay at the same point in the animation sequence.
		//crntFrame = STANDING_FRAME;
		//doesn't work well if the entity is moving, better to keep the same animation frame to
		//make walking look more natural
	}
	
	public void stopAnimation()
	{
		crntFrame = STANDING_FRAME;
		stepDistAccumulated = 0;
		walking = false;
	}	
	
	public void accumulateDistance(float dist)
	{
		stepDistAccumulated += dist;
		
		if(stepDistAccumulated >= stepLength/2)
		{
			incrementFrame();
			stepDistAccumulated -= stepLength/2;
		}

	}
}
