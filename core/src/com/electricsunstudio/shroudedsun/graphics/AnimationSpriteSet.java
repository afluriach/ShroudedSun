package com.electricsunstudio.shroudedsun.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;

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
		Graphics.drawTextureRegion(frames[frameNum], centerPos, batch, rotation);
	}
}
