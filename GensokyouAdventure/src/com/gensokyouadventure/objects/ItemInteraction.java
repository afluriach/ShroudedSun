package com.gensokyouadventure.objects;


public interface ItemInteraction
{
	public boolean canInteract(GameObject obj, Player player);
	public void interact(GameObject obj, Player player);
	public String interactMessage();
}
