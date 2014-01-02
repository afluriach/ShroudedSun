package com.pezventure.AI;

import com.pezventure.objects.Entity;

public class AI_Util
{
	public static void rotate(boolean clockwise, Entity entity)
	{
		int newDir = entity.getDir();
		
		if(clockwise)
		{
			newDir += 2;
			if(newDir >= 8) newDir -= 8;
		}
		else
		{
			newDir -= 2;
			if(newDir < 0) newDir += 8;
		}
		entity.setDesiredDir(newDir);
	}

}
