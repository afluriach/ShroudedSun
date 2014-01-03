package com.gensokyouadventure.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;

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

	public static void drawTextureRegion(TextureRegion region, Vector2 centerPos, SpriteBatch batch)
	{
		int centerPixX = (int) (centerPos.x*Game.PIXELS_PER_TILE);
		int centerPixY = (int) (centerPos.y*Game.PIXELS_PER_TILE);

		//lower left corner
		int x = centerPixX - region.getRegionWidth()/2;
		int y = centerPixY - region.getRegionHeight()/2;
		
		//batch.draw(texture, x, y);
		batch.draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
	}
		
	public static void drawTextureRegion(TextureRegion region, Vector2 centerPos, SpriteBatch batch, float rotation)
	{
		int centerPixX = (int) (centerPos.x*Game.PIXELS_PER_TILE);
		int centerPixY = (int) (centerPos.y*Game.PIXELS_PER_TILE);

		//lower left corner
		int x = centerPixX - region.getRegionWidth()/2;
		int y = centerPixY - region.getRegionHeight()/2;

		batch.draw(region, x, y, centerPixX, centerPixY, region.getRegionWidth(), region.getRegionHeight(), 1f, 1f, rotation);
	}

	
	/**
	 * requires ShapeRenderer batch.
	 */
	public static void drawCircle(ShapeRenderer renderer, Circle circle)
	{
		renderer.circle(circle.x, circle.y, circle.radius);
	}
	
	/**
	 * 
	 * @param h [0.0 to 360.0)
	 * @param s [0.0 to 1.0]
	 * @param v [float 0.0 to 1.0]
	 * @param a [float 0.0 to 1.0]
	 * @return
	 */
	public static Color hsva(float h, float s, float v, float a)
	{
		float r1, g1, b1;
		float C = v*s;
		float hPrime = h / 60;
		float x = C*(1-Math.abs(hPrime % 2.0f - 1));
		float m = v - C;
		
		if(s == 0)
		{
			//hue is undefined and no color will be added
			r1 = g1 = b1 = 0;
		}
		else if(0 <= hPrime && hPrime < 1)
		{
			r1 = C;
			g1 = x;
			b1 = 0;
		}
		else if(1 <= hPrime && hPrime < 2)
		{
			r1 = x;
			g1 = C;
			b1 = 0;
		}
		else if(2 <= hPrime && hPrime < 3)
		{
			r1 = 0;
			g1 = C;
			b1 = x;
		}
		else if(3 <= hPrime && hPrime < 4)
		{
			r1 = 0;
			g1 = x;
			b1 = C;
		}
		else if(4 <= hPrime && hPrime < 5)
		{
			r1 = x;
			g1 = 0;
			b1 = C;
		}
		else if(5 <= hPrime && hPrime < 6)
		{
			r1 = C;
			g1 = 0;
			b1 = x;
		}
		else
		{
			throw new IllegalArgumentException(String.format("Illegal hue given: %f", h));
		}
		
		return new Color(r1+m, g1+m, b1+m, a);
	}
	
	TextureRegion[] textureRegionFramesFromSheet(Texture spriteSheet, int length, int startingX, int startingY, int spriteHeight, int spriteWidth, int spacing)
	{
		TextureRegion[] frames = new TextureRegion[length];
		
		for(int i=0; i < length; ++i)
		{
			frames[i] = new TextureRegion(spriteSheet, startingX+spriteWidth*i+spacing*i, startingY, spriteWidth, spriteHeight);
		}
		
		return frames;
		
	}
}
