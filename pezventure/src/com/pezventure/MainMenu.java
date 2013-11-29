package com.pezventure;

public class MainMenu extends Menu
{
	public MainMenu(int screenWidth, int screenHeight)
	{
		super(screenWidth, screenHeight);
		
		heading = Game.TAG;
		menuEntries = new String[3];
		menuEntries[0] = "Start";
		menuEntries[1] = "Dev Start";
		menuEntries[2] = "Exit";
	}

	@Override
	public void handlePress(int menuOption)
	{
		switch(menuOption)
		{
		case 0:
			//start game
		case 1:
			//map select
		case 2:
			//exit
		}
	}	

}
