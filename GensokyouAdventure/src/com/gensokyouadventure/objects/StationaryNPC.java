package com.gensokyouadventure.objects;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.map.TilespaceRectMapObject;

//acts like a switch. NPC will have two different dialog options depending on whether or not it has been "activated".
public class StationaryNPC extends NPC implements Switch
{
	String msg1;
	String msg2;
	boolean activated;
	
	SwitchListener switchListener;
	ClearListener clearListener;
	
	//TODO base and unlocked msg.
	//move switch logic from door/barrier into separate class. use here.
	
	public StationaryNPC(TilespaceRectMapObject to)
	{
		super(to);
		
		msg1 = to.prop.get("msg1", String.class);
		msg2 = to.prop.get("msg2", String.class);
		
		if(to.prop.containsKey("switch"))
			switchListener = new SwitchListener(to.prop.get("switch", String.class));
		
		if(to.prop.containsKey("cleared"))
			clearListener = new ClearListener(to.prop.get("cleared", String.class));
	}
	
	@Override
	public void update()
	{
		if(switchListener != null) switchListener.update();
		if(clearListener != null) clearListener.update();
		
		if(switchListener != null || clearListener != null)
		{
			activated = Util.switchClearActivation(switchListener, clearListener);
		}
		
		super.update();
	}

	@Override
	public void talk()
	{
		Game.inst.setDialogMsg(activated ? msg2 : msg1);
	}

	@Override
	public void handleContact(GameObject other){
	}

	@Override
	public void handleEndContact(GameObject other) {
	}

	@Override
	public void init() {
		if(switchListener != null) switchListener.init();
		
	}

	@Override
	public boolean isActivated() {
		return activated;
	}
	
}
