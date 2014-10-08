package com.electricsunstudio.shroudedsun.objects.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.dialog.Dialog;
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
	
	protected boolean activated;
	
	SwitchListener switchListener;
	ClearListener clearListener;
	
	public StationaryNPC(TilespaceRectMapObject to)
	{
		super(to, BodyType.StaticBody);

		initDialog(to);
		initSwitch(to);
		
	}

    public StationaryNPC(TilespaceRectMapObject to, String entity)
    {
        //public NPC(Vector2 pos, String name, int startingDir, String animation, boolean stationary)
        super(to.rect.getCenter(new Vector2()), to.name, 2, entity, BodyType.StaticBody);
		initDialog(to);
		initSwitch(to);
    }
	
	void initDialog(TilespaceRectMapObject to)
	{
		//if dialogs are not defined, use name1 and name2 for the default
		dialog1 = to.prop.containsKey("dialog1") ? to.prop.get("dialog1", String.class) : to.name + "1";
		dialog2 = to.prop.containsKey("dialog2") ? to.prop.get("dialog2", String.class) : to.name + "2";
		
		Game.log(String.format("%s: %s,%s", to.name, dialog1, dialog2));
	}
	
	void initSwitch(TilespaceRectMapObject to)
	{
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
