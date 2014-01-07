package com.gensokyouadventure.objects.interaction;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.entity.NPC;
import com.gensokyouadventure.objects.entity.Player;

public class Talk implements ItemInteraction
{
	public boolean canInteract(GameObject obj, Player player)
	{
		return true;
	}

	public void interact(GameObject obj, Player player)
	{
		((NPC)obj).talk();
	}

	public String interactMessage()
	{
		return "Talk";
	}

}
