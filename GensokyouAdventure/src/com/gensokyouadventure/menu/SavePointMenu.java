package com.gensokyouadventure.menu;

import javax.swing.MenuSelectionManager;

import com.gensokyouadventure.Game;

public class SavePointMenu extends TextListMenu
{
	static final String[] entries = {"Select Character", "Exit"};
	
	public SavePointMenu()
	{
		super("Save Point", entries);
	}
	
	@Override
	public TextListMenu handleSelection(int menuOption)
	{
		switch(menuOption)
		{
		case 0:
			return new DevCharacterSelectMenu();
		case 1:
		default:
			Game.inst.menuHandler.closeMenu = true;
			return null;
		}		
	}

	@Override
	public boolean onBack()
	{
		//allow menu to be exited
		return true;
		
	}

}
