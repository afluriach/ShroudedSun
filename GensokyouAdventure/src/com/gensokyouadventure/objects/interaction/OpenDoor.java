package com.gensokyouadventure.objects.interaction;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.entity.Player;
import com.gensokyouadventure.objects.environment.Door;

public class OpenDoor implements ItemInteraction
{

	@Override
	public boolean canInteract(GameObject obj, Player player)
	{
		return true;
	}

	@Override
	public void interact(GameObject obj, Player player)
	{
		Door door = (Door) obj;
		
		if(door.isLocked())
		{
			Game.inst.showTextBox("The door is currently barred shut.");
		}
		else
		{
			if(door.getDestLink() != null)
			{
				Game.inst.setTeleporDestination(door.getDestMap(), door.getDestLink());
			}
			else
			{
				//door is current 1 unit wide
				player.setPos(door.getCenterPos().add(player.getFacingVector()));
			}
		}
	}

	@Override
	public String interactMessage()
	{
		return "Open";
	}

}
