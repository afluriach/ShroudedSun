package com.pezventure.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.physics.PrimaryDirection;

public class ProjectileSpriteSet
{
	//the direction that the projectile is facing as it is drawn.
	PrimaryDirection canonicalDirection;
	
	TextureRegion[] frames;
	int spriteHeight;
	int spriteWidth;
	
	public ProjectileSpriteSet(TextureRegion[][] frames)
	{
		int height = frames.length;
		int length;
		if(height == 0)
			throw new IllegalArgumentException("empty frames");
		else
		{
			length = frames[0].length;
			
			if(length==0)
				throw new IllegalArgumentException("empty frames");
			else
				spriteHeight = frames[0][0].getRegionHeight();
				spriteWidth = frames[0][0].getRegionWidth();
		}
		
		this.frames = new TextureRegion[height*length];
		
		for(int i=0, k=0;i<height;++i)
		{
			for(int j=0;j<length;++j, ++k)
			{
				this.frames[k] = frames[i][j];
			}
		}
	}	
	
	public void render(SpriteBatch batch, int frameNum, float rotation, Vector2 centerPos)
	{
		batch.draw(frames[frameNum], centerPos.x - spriteWidth/2, centerPos.y - spriteHeight/2, spriteWidth/2, spriteHeight/2, spriteWidth, spriteHeight, 1f, 1f, rotation, false);
	}
}
