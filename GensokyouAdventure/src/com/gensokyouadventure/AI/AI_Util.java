package com.gensokyouadventure.AI;

import com.gensokyouadventure.objects.entity.Entity;

public class AI_Util
{
	public static void rotate(boolean clockwise, Entity entity)
	{
		int newDir = entity.getNearestDir();
		
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
