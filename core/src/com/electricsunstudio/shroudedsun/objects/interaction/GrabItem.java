package com.electricsunstudio.shroudedsun.objects.interaction;

import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;


public class GrabItem implements ItemInteraction
{
	public boolean canInteract(GameObject obj, Player player)
	{
		if(!(obj instanceof Grabbable))
		{
			throw new RuntimeException(String.format("Invalid operation grab on object %s, class %s.", obj.getName(), obj.getClass().toString()));
		}
		else
		{
			return ((Grabbable) obj).canGrab();
		}
	}
	
	public void interact(GameObject obj, Player player)
	{
		player.holdingItem = obj;
		((Grabbable)obj).onGrab();
	}
	
	public String interactMessage()
	{
		return "Grab";
	}
}
