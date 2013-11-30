package com.pezventure.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.physics.PrimaryDirection;

public class EntityAnimation8Dir
{
	//the frame in the animation where the character is standing on two legs
	//i.e. the frame to draw when the entity is not mvoing
	private static final int STANDING_FRAME = 1;
	private static final int textureSize = 32;
	
	private EntitySpriteSet8Dir spriteSet;
	
	/**
	 * angle in degrees divided by 45.
	 */
	private int crntDirection;
	private int crntFrame = STANDING_FRAME;
	private int crntForm = 0;
	
	public EntityAnimation8Dir(EntitySpriteSet8Dir s, int startingDir)
	{
		crntDirection = startingDir;
		if(s==null)
			throw new NullPointerException("null entity sprite set given");
		spriteSet = s;
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
		TextureRegion region = spriteSet.sprites[crntForm][getSpriteRow()][crntFrame];
		
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
		
//		if(crntDirection <= 4)
//			return Math.abs(crntDirection - 2);	
//		else
//			return Math.abs(crntDirection - 4);
	}
	
	public void setDirection(int newDir)
	{
		crntDirection = newDir;
//		crntFrame = STANDING_FRAME;
		//when changing direction, stay at the same point in the animation sequence.
	}
	
	public void resetAnimation()
	{
		crntFrame = STANDING_FRAME;
	}
	
	public void setForm(int form)
	{
		if(form < 0 || form >= spriteSet.sprites.length)
			throw new IllegalArgumentException("form number out of bounds: " + form);
		
		crntForm = form;
	}
}
