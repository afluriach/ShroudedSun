package com.gensokyouadventure.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.physics.PrimaryDirection;

/**
 * simple text-based menu. presents a list of options and allows the player to move a cursor and select one.
 * @author ant
 *
 */
public abstract class TextListMenu
{
	static final int lineSeparation = 30;
	static final int horizontalMargin = 20;
	static final float cursorMoveInterval = 0.25f;
	static final int trimMargin = 5;
	static final int trimThickness = 2;
		
	String [] menuEntries;
	String heading;
	//which item currently has the cursor next to it.
	int selectedMenuItem;
	
	//controls
	boolean upHeld;
	boolean downHeld;
	float cursorMoveTime;
	
	public TextListMenu(String heading, String[] menuEntries)
	{
		this.heading = heading;
		this.menuEntries = menuEntries;
		
	}
	
	public void render(SpriteBatch sb, BitmapFont font, ShapeRenderer sr)
	{
		Vector2 center = new Vector2(Game.inst.screenWidth/2, Game.inst.screenHeight/2);
		
		int totalLines = menuEntries.length + 2;
		
		int firstLineY = (int) (center.y + lineSeparation*totalLines/2.0f);
		float longestLineWidth = 0;
		
		for(int i=0;i<menuEntries.length; ++i)
		{
			//look at each string to be drawn and find out which is the longest. this is the line width that will
			//be used in the pos rect (to draw the trim). thus the trim will fit based on string length
			FloatArray positions = new FloatArray();
			font.computeGlyphAdvancesAndPositions(menuEntries[i], new FloatArray(), positions);
			float lastCharPos = positions.items[positions.size -1];
			
			if(lastCharPos > longestLineWidth) longestLineWidth = lastCharPos;
			
//			Game.log(String.format("entry %d: %s, pos: %f", i, menuEntries[i], longestLineWidth));
		}
		
		//do the same for the heading line
		if(heading != null)
		{
			FloatArray positions = new FloatArray();
			font.computeGlyphAdvancesAndPositions(heading, new FloatArray(), positions);
			float lastCharPos = positions.items[positions.size -1];
			
			if(lastCharPos > longestLineWidth) longestLineWidth = lastCharPos;
			
			//Game.log(String.format("heading: %s, pos: %f", heading, longestLineWidth));

		}

		Rectangle pos = new Rectangle();
		pos.x = center.x - longestLineWidth/2 - trimMargin;
		pos.y = center.y - totalLines/2.0f*lineSeparation - trimMargin;
		pos.width = longestLineWidth + 2*trimMargin;
		pos.height = totalLines*lineSeparation + 2*trimMargin;
		
		sb.begin();
		
		//draw black background
		sr.begin(ShapeType.Filled);
			sr.setColor(Color.BLACK);
			sr.rect(pos.x, pos.y, pos.width, pos.height);
		sr.end();
		
		//draw white trim
		sr.begin(ShapeType.Line);		
			sr.setColor(Color.WHITE);
			sr.rectLine(pos.x, pos.y, pos.x+pos.width, pos.y, trimThickness);
			sr.rectLine(pos.x+pos.width, pos.y, pos.x+pos.width, pos.y+pos.height, trimThickness);
			sr.rectLine(pos.x+pos.width, pos.y+pos.height, pos.x, pos.y+pos.height, trimThickness);
			sr.rectLine(pos.x, pos.y+pos.height, pos.x, pos.y, trimThickness);		
		sr.end();
		
		//draw text. 
		font.setColor(Color.WHITE);
		if(heading != null)
		{
			font.draw(sb, heading, center.x - longestLineWidth/2, firstLineY);
		}
		
		for(int i=0;i<menuEntries.length; ++i)
		{
			font.setColor(i==selectedMenuItem ? Graphics.hsva(0f, 0f, 1f, 1f) : Graphics.hsva(0f, 0f, 0.7f, 1f));

			font.draw(sb, menuEntries[i], center.x - longestLineWidth/2, firstLineY - lineSeparation*(i+2));
		}		
		sb.end();
	}
	
	public void update()
	{
		//handle input. add delay so cursor doesn't move too fast.
		
		//no, assume update is already being called reguarly in the game loop
		//Game.inst.controls.update();
		
		if(Game.inst.touchControls)
		{
			//check for touch press to select menu item
		}
		if(Game.inst.keyControls)
		{
			//check key controls (gamepad presses are included in key press state)
			
			PrimaryDirection controlDir = Game.inst.controls.controlPadDirection();
			
			cursorMoveTime += Game.SECONDS_PER_FRAME;
			
			if(cursorMoveTime > cursorMoveInterval)
			{
				if(controlDir == PrimaryDirection.up && !upHeld)
				{
					//move cursor up
					selectedMenuItem--;
					if(selectedMenuItem < 0) selectedMenuItem = menuEntries.length - 1;
					
					upHeld = true;
					cursorMoveTime = 0;
				}
				else upHeld = controlDir == PrimaryDirection.up;
				
				if(controlDir == PrimaryDirection.down && !downHeld)
				{
					selectedMenuItem++;
					if(selectedMenuItem == menuEntries.length) selectedMenuItem = 0;
					
					downHeld = true;
				}
				else downHeld = controlDir == PrimaryDirection.down;
			}	
			
		}
		
		//handle menu selection if applicable. this may result in another menu being opened (sub-menu).
		//also want to remember previous menu screen in order to go back.
		
		//render
	}
	
	//for touch based controls. could allow the player to select an option by touching it. 
	//maybe not work well depending on text size
	/**
	 * 
	 * @param x touch pos
	 * @param y touch pos, where 0 bottom of screen.
	 * @return index of menu entry that was touched, or -1 for no result
	 */
//	public int handleTouch(int x, int y)
//	{
//		return -1;
//	}
	
	public TextListMenu onSelectButton()
	{
		return handleSelection(selectedMenuItem);
	}
	
	/**
	 * 
	 * @param menuOption the position of the item in the menu pressed.
	 * @return the new menu frame to be displayed (if applicable). 
	 */
	public abstract TextListMenu handleSelection(int menuOption);
	public abstract boolean onBack();
	
}
