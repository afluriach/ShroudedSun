package com.pezventure.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.physics.PrimaryDirection;

public class Animation 
{
	AnimationSpriteSet spriteSet;
	float frameIntervalLength;
	float frameTimeAccumulated;
	PrimaryDirection direction = PrimaryDirection.up;
	int currentFrame;
	
	public Animation(AnimationSpriteSet spriteSet, float frameTime, PrimaryDirection direction)
	{
		this.spriteSet = spriteSet;
		this.direction = direction;
		frameIntervalLength = frameTime;
		frameTimeAccumulated = 0;
		currentFrame = 0;
	}
	
	public void advance(float dt)
	{
		frameTimeAccumulated += dt;
		while(frameTimeAccumulated >= frameIntervalLength)
		{
			++currentFrame;
			frameTimeAccumulated -= frameIntervalLength;
			
			if(currentFrame == spriteSet.frames.length)
				currentFrame = 0;
		}
	}
	
	public void render(Vector2 centerPos, SpriteBatch batch)
	{
		float rotation = direction.getAngle() - spriteSet.canonicalDirection.getAngle();
		spriteSet.render(batch, currentFrame, rotation, centerPos);
	}
}
