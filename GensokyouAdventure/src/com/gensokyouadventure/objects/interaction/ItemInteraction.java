package com.gensokyouadventure.objects.interaction;

import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.entity.Player;


public interface ItemInteraction
{
	public boolean canInteract(GameObject obj, Player player);
	public void interact(GameObject obj, Player player);
	public String interactMessage();
}
