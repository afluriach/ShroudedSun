package com.gensokyouadventure.menu;

import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gensokyouadventure.Game;


//class for handling selection menus and interactive text dialogs. stack based to support opening submenus, as well as 
//closing and returning to the previously opened menu.
public class MenuHandler
{
	//menu is active if the frame stack is not empty
	Stack<TextListMenu> frameStack = new Stack<TextListMenu>();
	public boolean closeMenu;
	
	boolean selectHeld = false;
	boolean backHeld = false;
	
	/**
	 * clears the menu stack and sets the given menu frame as the current menu frame.
	 * @param frame
	 */
	public void setTopLevelMenu(TextListMenu frame)
	{
		frameStack.clear();
		frameStack.push(frame);
	}
	
	public void openSubMenu(TextListMenu frame)
	{
		frameStack.push(frame);
	}
	
	//submenu may call this unconditionally upon selecting cancel or go back if it doesn't know how it was opened. 
	public void closeSubMenu()
	{
		if(!frameStack.isEmpty())
			frameStack.pop();
	}
	
	public void closeMenu()
	{
		frameStack.clear();
	}
	
	public void update()
	{
		TextListMenu crntFrame = frameStack.peek();
				
		crntFrame.update();
		
		if(Game.inst.controls.backButton() && !backHeld)
		{
			if(crntFrame.onBack())
				closeSubMenu();
			backHeld = true;
		}
		else backHeld = Game.inst.controls.backButton();
		
		if(Game.inst.controls.selectButton() && !selectHeld)
		{
			//a selection may result in a submenu being selected
			TextListMenu newMenu = crntFrame.onSelectButton();
			
			if(newMenu != null)
			{
				frameStack.push(newMenu);
			}
			
			selectHeld = true;
		}
		else selectHeld = Game.inst.controls.selectButton();
		
		if(closeMenu)
		{
			closeMenu();
			closeMenu = false;
		}
	}
	
	public boolean menuActive()
	{
		return !frameStack.isEmpty();
	}

	public void render(SpriteBatch sb, BitmapFont font, ShapeRenderer sr)
	{
		frameStack.peek().render(sb, font, sr);
	}
}
