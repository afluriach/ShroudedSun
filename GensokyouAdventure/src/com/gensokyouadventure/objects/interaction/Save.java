package com.gensokyouadventure.objects.interaction;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.menu.SavePointMenu;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.entity.characters.Player;

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
