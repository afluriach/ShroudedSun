package com.pezventure.objects;

import com.pezventure.Game;
import com.pezventure.Util;

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
			Game.inst.setDialogMsg("The door is currently barred shut.");
		}
		else
		{
			if(door.destLink != null)
			{
				Game.inst.setTeleporDestination(door.destMap, door.destLink);
			}
			else
			{
				player.setPos(door.getCenterPos().add(Util.get8DirUnit(player.getDir())));
			}
		}
	}

	@Override
	public String interactMessage()
	{
		return "Open";
	}

}
