package com.pezventure.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pezventure.Game;
import com.pezventure.physics.PrimaryDirection;

public class EntitySpriteSet4Dir
{
	TextureRegion[][] sprites;
	public final int spriteSize;
	public final int animationLen;
	
	public EntitySpriteSet4Dir(TextureRegion[][] spriteSheetSlices, int tileX, int tileY, int spriteSize, int animationLen)
	{
		this.spriteSize = spriteSize;
		this.animationLen = animationLen;
		
		sprites = new TextureRegion[4][animationLen];
		
		loadRow(spriteSheetSlices, PrimaryDirection.down, tileX, tileY, animationLen);
		loadRow(spriteSheetSlices, PrimaryDirection.left, tileX, tileY+1, animationLen);
		loadRow(spriteSheetSlices, PrimaryDirection.right, tileX, tileY+2, animationLen);
		loadRow(spriteSheetSlices, PrimaryDirection.up, tileX, tileY+3, animationLen);
	}
	
	void loadRow(TextureRegion[][] spriteSheetSlices, PrimaryDirection dir, int tileX, int tileY, int animationLen)
	{
		int dirNum = dir.getInt();
		for(int col = tileX, frame=0; frame < animationLen; ++col, ++frame)
		{
			sprites[dirNum][frame] = spriteSheetSlices[tileY][col];
		}
	}
}
