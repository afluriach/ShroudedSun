package com.electricsunstudio.shroudedsun.objects.interaction;

import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;


public interface ItemInteraction
{
	public boolean canInteract(GameObject obj, Player player);
	public void interact(GameObject obj, Player player);
	public String interactMessage();
}
