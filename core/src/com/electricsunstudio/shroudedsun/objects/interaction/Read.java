package com.electricsunstudio.shroudedsun.objects.interaction;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;
import com.electricsunstudio.shroudedsun.objects.environment.Sign;

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
