package com.pezventure.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pezventure.Game;
import com.pezventure.physics.PrimaryDirection;

public class EntitySpriteSet8Dir
{
	public static final int NO_GLOW = 0;
	public static final int RED_GLOW = 1;
	public static final int BLUE_GLOW = 2;
	
	
	TextureRegion[][][] sprites;
	public final int spriteSize;
	public final int animationLen;
	
	public EntitySpriteSet8Dir(Texture texture, int tileX, int tileY, int spriteSize, int animationLen)
	{
		this.spriteSize = spriteSize;
		this.animationLen = animationLen;
		
		sprites = new TextureRegion[3][5][animationLen];
		
		loadCharacter(tileX, tileY, animationLen, texture);
	}
	
	void loadCharacter(int gridX, int gridY, int animationLen, Texture texture)
	{
		int charStartingY = 10+196*gridY;
		int charStartingX = 1+333*gridX;
		
		for(int i=0;i<3; ++i)
		{
			//load each of the 3 forms. no glow, red glow, and blue glow
			
			//forms are arranged horizontally, but use the same starting Y
			int formStartingX = charStartingX+37*3*i;
			
			for(int j=0;j<5; j++)
			{
				//load each of the 5 directions
				
				//directions are arranged vertically, but use the same starting X
				int dirStartingY = charStartingY + 37*j;
				
				for(int k=0;k<animationLen; ++k)
				{
					//for a given form and direction, load each animation frame
					//animation frames are arranged horizontally
					
					int frameStartingX = formStartingX + 37*k;
					
					sprites[i][j][k] = new TextureRegion(texture, frameStartingX, dirStartingY, 36, 36);
				}
				
			}
		}
	}
	
}
