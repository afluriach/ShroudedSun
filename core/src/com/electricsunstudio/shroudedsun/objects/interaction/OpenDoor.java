package com.electricsunstudio.shroudedsun.objects.interaction;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.Util;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;
import com.electricsunstudio.shroudedsun.objects.environment.Door;

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
				Game.inst.setTeleportDestination(door.getDestMap(), door.getDestLink());
			}
			else
			{
				//door is current 1 unit wide. if a double door, proceed two units instead
				player.setPos(door.getCenterPos().add(player.getFacingVector().scl(door.doubleDoor ? 2f : 1f)));
			}
		}
	}

	@Override
	public String interactMessage()
	{
		return "Open";
	}

}
