package com.gensokyouadventure.menu;

import com.gensokyouadventure.Game;

public class LevelSelectMenu extends TextListMenu
{
	public LevelSelectMenu()
	{
		super("Area Select", Game.inst.areaLoader.getAreas());
	}

	@Override
	public TextListMenu handleSelection(int menuOption) {
		//return a submenu that will show all maplinks in the selected area.
		return new LinkSelectMenu(menuEntries[menuOption]);
	}

	@Override
	public boolean onBack() {
		return true;
	}

}
