package com.electricsunstudio.shroudedsun.objects.entity;

import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.Dialog;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.Util;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.ClearListener;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.SwitchListener;
import com.electricsunstudio.shroudedsun.objects.environment.Switch;

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

    public StationaryNPC(TilespaceRectMapObject to, String entity)
    {
        //public NPC(Vector2 pos, String name, int startingDir, String animation, boolean stationary)
        super(to.rect.getCenter(new Vector2()), to.name, 2, entity, true);
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
