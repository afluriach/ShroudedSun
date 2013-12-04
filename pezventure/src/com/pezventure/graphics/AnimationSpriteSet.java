package com.pezventure.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.physics.PrimaryDirection;

public class AnimationSpriteSet
{
	//the direction that the projectile is facing as it is drawn.
	PrimaryDirection canonicalDirection;
	
	TextureRegion[] frames;
	
	int height, width;
	
	public AnimationSpriteSet(TextureRegion[] frames, PrimaryDirection drawnDirection)
	{
		canonicalDirection = drawnDirection;
		this.frames = frames;
		
		height = frames[0].getRegionHeight();
		width =  frames[0].getRegionWidth();
	}	
	
	public void render(SpriteBatch batch, int frameNum, float rotation, Vector2 centerPos)
	{
		batch.draw(frames[frameNum], centerPos.x - width/2, centerPos.y - height/2, width/2, height/2, width, height, 1f, 1f, rotation, false);
	}
}
