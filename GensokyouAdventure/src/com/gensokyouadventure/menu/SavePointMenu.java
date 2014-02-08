package com.gensokyouadventure.menu;

import javax.swing.MenuSelectionManager;

import com.gensokyouadventure.Game;

public class SavePointMenu extends TextListMenu
{
	static final String[] entries = {"Save", "Select Character", "Exit"};
	String savePointName;
	
	public SavePointMenu(String savePointName)
	{
		super("Save Point", entries);
		
		this.savePointName = savePointName;
	}
	
	@Override
	public TextListMenu handleSelection(int menuOption)
	{
		switch(menuOption)
		{
		case 0:
			Game.inst.saveGame(savePointName);
			Game.inst.menuHandler.closeMenu = true;
		case 1:
			return new DevCharacterSelectMenu();
		case 2:
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
