package com.electricsunstudio.shroudedsun.objects.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.AI.NPC_FSM;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.GameObject;

public class NPC extends Entity
{	
	public NPC(TilespaceRectMapObject to, BodyType bodyType)
	{
		super(to, to.prop.get("sprite", String.class), "npc", bodyType);
	}
	
	public NPC(TilespaceRectMapObject to, String entity, BodyType bodyType)
	{
		super(to, entity, "npc", bodyType);
	}
    	
	public NPC(Vector2 pos, String name, int startingDir, String animation, BodyType bodyType)
	{
		super(pos, animation, startingDir, name, "npc", bodyType);
		speed = defaultSpeed;
	}
	
	public void talk()
	{
		if(hasNpcFsm())
		{
			String dialog = getFsm().getDialog();
			Game.inst.setDialog(dialog, character);
		}
	}
	
	public boolean canTalk()
	{
		if(hasNpcFsm())
		{
			return getFsm().canTalk();
		}
		else
		{
			Game.log("npc can't talk");
			return false;
		}
	}

	@Override
	public void handleContact(GameObject other) {
	}

	@Override
	public void handleEndContact(GameObject other) {
	}
	
	//assuming that for an NPC, it actually has an NPC_FSM
	//else we will find out via a runtime exception
	boolean hasNpcFsm()
	{
		return fsm != null && fsm instanceof NPC_FSM;
	}
	
	NPC_FSM getFsm()
	{
		return (NPC_FSM) fsm;
	}	
}
