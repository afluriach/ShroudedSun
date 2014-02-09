package com.gensokyouadventure.objects.environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.GameObject;

public class TreasureChest extends GameObject
{
	boolean opened = false;
	
	Texture closed = Game.inst.spriteLoader.getTexture("closed_chest32");
	Texture open = Game.inst.spriteLoader.getTexture("open_chest32");
	
	//if non-zero, the treasure chest has gold in it
	public int gold=0;
	
	//if non-zero, the chest has the upgrade in it.
	public int hp_up = 0;
	public int mp_up = 0;
	
	
	public TreasureChest(TilespaceRectMapObject mo)
	{
		super(mo);
		
		physicsBody = Game.inst.physics.addRectBody(mo.rect, this, BodyType.StaticBody, "environmental_floor");
		
		if(mo.prop.containsKey("gold")) gold = Integer.parseInt(mo.prop.get("gold", String.class));
		
		if(mo.prop.containsKey("hp_up")) hp_up = Integer.parseInt(mo.prop.get("hp_up", String.class));
		if(mo.prop.containsKey("mp_up")) mp_up = Integer.parseInt(mo.prop.get("mp_up", String.class));
		//TODO hp and mp upgrade
	}

	@Override
	public void update() {
	}

	@Override
	public void render(SpriteBatch sb) {
		Graphics.drawTexture(opened ? open : closed, getCenterPos(), sb);
	}

	@Override
	public void handleContact(GameObject other) {
	}

	@Override
	public void handleEndContact(GameObject other) {
	}

	@Override
	public void init() {
	}
	
	public boolean opened()
	{
		return opened;
	}
	
	public void open()
	{
		//set chest as opened
		opened = true;
	}

}
