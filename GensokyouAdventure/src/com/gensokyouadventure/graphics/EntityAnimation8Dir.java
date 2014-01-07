package com.gensokyouadventure.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;

public class EntityAnimation8Dir
{
	//the frame in the animation where the character is standing on two legs
	//i.e. the frame to draw when the entity is not moving
	private static final int STANDING_FRAME = 1;
	
	private EntitySpriteSet8Dir spriteSet;
	
	/**
	 * angle in degrees divided by 45.
	 */
	private int crntDirection;
	private int crntFrame = STANDING_FRAME;
		
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
	
	public void incrementFrame()
	{		
		crntFrame++;
		if(crntFrame == spriteSet.animationLen) crntFrame = 0;
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
	
	public void resetAnimation()
	{
		crntFrame = STANDING_FRAME;
	}	
}
