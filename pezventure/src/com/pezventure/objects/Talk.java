package com.pezventure.objects;

import com.pezventure.Game;

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
