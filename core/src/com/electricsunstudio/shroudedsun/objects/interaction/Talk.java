package com.electricsunstudio.shroudedsun.objects.interaction;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.NPC;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;

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
