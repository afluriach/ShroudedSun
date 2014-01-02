package com.pezventure.objects;

import com.pezventure.Game;

public class Read implements ItemInteraction 
{
	public boolean canInteract(GameObject obj, Player player)
	{
		return true;
	}

	@Override
	public void interact(GameObject obj, Player player)
	{
		Game.inst.setDialogMsg( ((Sign)obj).msg );
	}

	@Override
	public String interactMessage()
	{
		return "Read";
	}

}