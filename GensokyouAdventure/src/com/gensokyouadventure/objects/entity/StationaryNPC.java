package com.gensokyouadventure.objects.entity;

import com.gensokyouadventure.Dialog;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.ClearListener;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.SwitchListener;
import com.gensokyouadventure.objects.environment.Switch;

//acts like a switch. NPC will have two different dialog options depending on whether or not it has been "activated".
public class StationaryNPC extends NPC implements Switch
{
	String dialog1, dialog2;
	
	boolean activated;
	
	SwitchListener switchListener;
	ClearListener clearListener;
	
	public StationaryNPC(TilespaceRectMapObject to)
	{
		super(to);
		
		dialog1 = to.prop.get("dialog1", String.class);
		dialog2 = to.prop.get("dialog2", String.class);

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
		Game.inst.setDialog(activated ? dialog2 : dialog1, character);
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

	@Override
	public boolean isPermanent() {
		return false;
	}

	@Override
	public void activate() {
		
	}
	
}
