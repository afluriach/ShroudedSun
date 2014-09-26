package com.electricsunstudio.shroudedsun.menu;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.entity.characters.EChar;
import com.electricsunstudio.shroudedsun.objects.entity.characters.PlayableCharacter;

//show a list of all characters
public class DevCharacterSelectMenu extends TextListMenu
{
	static final String[] entries = {"Alice", "Aya", "Cirno","Marisa", "Reimu", "Youmu"};

	public DevCharacterSelectMenu()
	{
		super("Character", entries);
	}
	
	@Override
	public TextListMenu handleSelection(int menuOption)
	{
		EChar ch = null;
		switch(menuOption)
		{
		case 0: ch = EChar.alice; break;
		case 1: ch = EChar.aya; break;
		case 2: ch = EChar.cirno; break;
		case 3: ch = EChar.marisa; break;
		case 4: ch = EChar.reimu; break;
		case 5: ch = EChar.youmu; break;
		}
		
		Game.inst.menuHandler.closeMenu = true;
		Game.inst.changeCharacter(ch);
		
		return null;
	}

	@Override
	public boolean onBack() {
		return true;
	}

}
