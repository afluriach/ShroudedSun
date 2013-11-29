package com.pezventure;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;

/**
 * list of menu options. ability to select and go back. 
 * @author ant
 *
 */
public abstract class Menu
{
	static final int lineWidth = 60;
	static final int lineSeparation = 30;
	static final int horizontalMargin = 20;
	
	int screenHeight;
	int screenWidth;
	
	protected Menu(int screenWidth, int screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}
	
	String [] menuEntries;
	String heading;
	
	public void render(SpriteBatch sb, BitmapFont font)
	{
		if(heading != null)
		{
			font.draw(sb, heading, horizontalMargin, screenHeight - lineSeparation);
		}
		
		for(int i=0;i<menuEntries.length; ++i)
		{
			font.draw(sb, menuEntries[i], horizontalMargin, screenHeight - lineSeparation*(i+2));
		}		
	}
	
	/**
	 * 
	 * @param x touch pos
	 * @param y touch pos, where 0 bottom of screen.
	 * @return index of menu entry that was touched, or -1 for no result
	 */
	public int handleTouch(int x, int y)
	{
		return -1;
	}
	
	/**
	 * 
	 * @param menuOption the position of the item in the menu pressed.
	 */
	public abstract void handlePress(int menuOption);
}
