package com.electricsunstudio.shroudedsun.menu;

import com.electricsunstudio.shroudedsun.Game;

public class LinkSelectMenu extends TextListMenu
{
	String area;
	
	public LinkSelectMenu(String area)
	{
		super(area, Game.inst.areaLoader.getLinksInArea(area));
	}

	@Override
	public TextListMenu handleSelection(int menuOption) {
		//start game at selected area and maplink
		Game.inst.menuHandler.closeMenu = true;
		
		Game.inst.loadAreaAtMaplink(area, menuEntries[menuOption]);
		
		return null;
	}

	@Override
	public boolean onBack() {
		return true;
	}

}
