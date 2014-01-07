package com.gensokyouadventure.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EntitySpriteSet8Dir
{
	public static final int NO_GLOW = 0;
	public static final int RED_GLOW = 1;
	public static final int BLUE_GLOW = 2;
	
	
	TextureRegion[][] sprites;
	public final int spriteSize;
	public final int animationLen;
	
	public EntitySpriteSet8Dir(Texture texture, int spriteSize, int animationLen)
	{
		this.spriteSize = spriteSize;
		this.animationLen = animationLen;
		
		sprites = new TextureRegion[5][3];
		
		for(int dir=0;dir < 4; ++dir)
		{
			for(int frame = 0; frame < 3; ++frame)
			{
				sprites[dir][frame] = new TextureRegion(texture, spriteSize*frame, spriteSize*dir, spriteSize, spriteSize);
			}
		}
		
		//dir4 is the last column, going down
		
		for(int frame=0; frame < 3; ++frame)
		{
			sprites[4][frame] = new TextureRegion(texture, 3*spriteSize, frame*spriteSize, spriteSize, spriteSize);
		}
	}	
}
