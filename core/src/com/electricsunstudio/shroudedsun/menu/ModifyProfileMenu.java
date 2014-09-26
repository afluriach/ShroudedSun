package com.electricsunstudio.shroudedsun.menu;

import com.electricsunstudio.shroudedsun.Game;

public class ModifyProfileMenu extends TextListMenu
{
	static final String[] options = {"Start", "Copy", "Delete", "Back"};
	int profileID;
	
	public ModifyProfileMenu(int profileID) {
		super("", options);
		
		//set the heading to the name of the profile.
		heading = Game.inst.loadSaveState(profileID).profileName;
		
		this.profileID = profileID;
	}

	@Override
	public TextListMenu handleSelection(int menuOption) {
		
		switch(menuOption)
		{
		case 0:
			//load game
			Game.inst.crntProfileID = profileID;
			Game.inst.menuHandler.closeMenu = true;
			Game.inst.loadGame();
		default:
			//go back. 
		}
		
		return null;
	}

	@Override
	public boolean onBack() {
		return true;
	}

}
