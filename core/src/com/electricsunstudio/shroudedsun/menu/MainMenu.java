package com.electricsunstudio.shroudedsun.menu;
import com.electricsunstudio.shroudedsun.Game;

public class MainMenu extends TextListMenu
{
	static final String[] entries = {"Select Profile", "Level Select", "Dev Start", "Exit"};
	
	public MainMenu()
	{
		super("Gensokyou Adventure", entries);
	}

	@Override
	public TextListMenu handleSelection(int menuOption)
	{
		switch(menuOption)
		{
		case 0:
			//start game from the beginning.
//			Game.inst.menuHandler.closeMenu = true; 
//			Game.inst.loadGameStart();
//			return null;
			return new ProfileSelectMenu();
		case 1:
			//start game from level select room
			Game.inst.menuHandler.closeMenu = true;
			Game.inst.loadLevelSelect();
			return null;
		case 2:
			//bring up menu of all loading areas.
			return new LevelSelectMenu();
		case 3:
			//exit
			Game.inst.exit();
			return null;
		default:
				return null;
		}
	}

	@Override
	public boolean onBack() {
		return false;
	}	

}
