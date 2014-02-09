package com.gensokyouadventure.objects.interaction;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.TextBox;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.entity.characters.Player;
import com.gensokyouadventure.objects.environment.TreasureChest;

public class OpenChest implements ItemInteraction {

	@Override
	public boolean canInteract(GameObject obj, Player player) {
		
		return !((TreasureChest)obj).opened();
	}

	@Override
	public void interact(GameObject obj, Player player) {
		
		TreasureChest chest = (TreasureChest) obj;
		
		// TODO open dialog showing contents of chest.
		//process whatever was found.
		
		if(chest.gold > 0)
		{
			Game.inst.activeTextBox = new TextBox(Game.inst.dialogPos, Game.inst.font, String.format("%d gold found.", chest.gold));
			Game.inst.saveState.gold += chest.gold;
		}
		else if(chest.hp_up > 0)
		{
			Game.inst.activeTextBox = new TextBox(Game.inst.dialogPos, Game.inst.font, String.format("HP_UP(%d) found. Your max HP has increased by %d to %d.", chest.hp_up, chest.hp_up, Game.inst.saveState.maxHP+chest.hp_up));
			Game.inst.saveState.maxHP += chest.hp_up;
		}
		else if(chest.mp_up > 0)
		{
			Game.inst.activeTextBox = new TextBox(Game.inst.dialogPos, Game.inst.font, String.format("MP_UP(%d) found. Your max MP has increased by %d to %d.", chest.mp_up, chest.mp_up, Game.inst.saveState.maxMP+chest.mp_up));
			Game.inst.saveState.maxMP += chest.mp_up;
		}
		
		chest.open();
	}

	@Override
	public String interactMessage() {
		return "Open";
	}

}
