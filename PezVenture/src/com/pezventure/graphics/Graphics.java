package com.pezventure.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;

public class Graphics 
{
	public static void drawTexture(Texture texture, Vector2 centerPos, SpriteBatch batch)
	{
		int centerPixX = (int) (centerPos.x*Game.PIXELS_PER_TILE);
		int centerPixY = (int) (centerPos.y*Game.PIXELS_PER_TILE);

		//lower left corner
		int x = centerPixX - texture.getWidth()/2;
		int y = centerPixY - texture.getHeight()/2;
		
		//batch.draw(texture, x, y);
		batch.draw(texture, x, y, texture.getWidth(), texture.getHeight());

	}
}
