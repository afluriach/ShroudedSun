package com.gensokyouadventure.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.physics.PrimaryDirection;

public class EntityAnimation4Dir
{
	//the frame in the animation where the character is standing on two legs
	//i.e. the frame to draw when the entity is not moving
	private static final int STANDING_FRAME = 1;
	
	private EntitySpriteSet4Dir spriteSet;
	private PrimaryDirection crntDirection;
	private int crntFrame = STANDING_FRAME;
	
	public EntityAnimation4Dir(EntitySpriteSet4Dir s, PrimaryDirection startingDir)
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
		
		batch.draw(spriteSet.sprites[crntDirection.getInt()][crntFrame], x, y);
	}
	
	public void setDirection(PrimaryDirection newDir)
	{
		crntDirection = newDir;
//		crntFrame = STANDING_FRAME;
		//when changing direction, stay at the same point in the animation sequence.
	}
	
	public void resetAnimation()
	{
		crntFrame = STANDING_FRAME;
	}
	
}
