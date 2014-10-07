package com.electricsunstudio.shroudedsun.AI;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.entity.Entity;

public abstract class NPC_FSM extends AI_FSM
{
	public NPC_FSM(Entity agent) {
		super(agent);
	}
	
	public boolean canTalk()
	{
		if(crntState instanceof NPC_State)
		{
			return ((NPC_State)crntState).canTalk();
		}
		else
		{
			return false;
		}
	}
	public String getDialog()
	{
		//in case the NPC has a non-dialog state
		if(crntState instanceof NPC_State)
		{
			return ((NPC_State)crntState).getDialog();
		}
		else return null;
	}
}
