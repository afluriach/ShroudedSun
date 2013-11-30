package com.pezventure.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.physics.PrimaryDirection;

public class EntityAnimation4Dir
{
	//the frame in the animation where the character is standing on two legs
	//i.e. the frame to draw when the entity is not mvoing
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
//		Gdx.app.log(Game.TAG, "inc frame: " + crntFrame);
	}
	
	public void render(SpriteBatch batch, Vector2 centerPos)
	{
//		Gdx.app.log(Game.TAG, spriteSet.toString());
		
		int centerPixX = (int) (centerPos.x*Game.PIXELS_PER_TILE);
		int centerPixY = (int) (centerPos.y*Game.PIXELS_PER_TILE);
		//lower left corner
		int x = centerPixX- spriteSet.spriteSize/2;
		int y = centerPixY - spriteSet.spriteSize/2;
		
		batch.draw(spriteSet.sprites[crntDirection.getInt()][crntFrame], x, y);
		//Gdx.app.log(Game.TAG, "animation pos: " + x + " " + y);
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
