package com.gensokyouadventure.objects.interaction;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.entity.characters.Player;
import com.gensokyouadventure.objects.environment.Sign;

public class Read implements ItemInteraction 
{
	public boolean canInteract(GameObject obj, Player player)
	{
		return true;
	}

	@Override
	public void interact(GameObject obj, Player player)
	{
		Game.inst.showTextBox( ((Sign)obj).getMsg() );
	}

	@Override
	public String interactMessage()
	{
		return "Read";
	}

}
