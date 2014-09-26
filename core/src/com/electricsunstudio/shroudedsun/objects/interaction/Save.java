package com.electricsunstudio.shroudedsun.objects.interaction;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.menu.SavePointMenu;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;

public class Save implements ItemInteraction {

	@Override
	public boolean canInteract(GameObject obj, Player player) {
		return true;
	}

	@Override
	public void interact(GameObject obj, Player player) {
		//bring up save point menu. 
		Game.inst.menuHandler.setTopLevelMenu(new SavePointMenu(obj.getName()));
	}

	@Override
	public String interactMessage() {
		return "Save";
	}

}
