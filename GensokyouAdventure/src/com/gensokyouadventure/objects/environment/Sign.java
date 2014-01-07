package com.gensokyouadventure.objects.environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.map.MapDataException;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.GameObject;

public class Sign extends GameObject
{
	String msg;
	Texture texture;
	
	public Sign(TilespaceRectMapObject mo) {
		super(mo);
		
		if(!mo.prop.containsKey("msg"))
		{
			throw new MapDataException("sign must have message on it");
		}
		msg = mo.prop.get("msg", String.class);
		physicsBody = Game.inst.physics.addRectBody(mo.rect, this, BodyType.StaticBody, "environmental_floor");
		texture = Game.inst.spriteLoader.getTexture("sign");
	}

	public String getMsg()
	{
		return msg;
	}
	
	@Override
	public void update() {
	}

	@Override
	public void render(SpriteBatch sb) {
		Graphics.drawTexture(texture, getCenterPos(), sb);
	}

	@Override
	public void handleContact(GameObject other) {
	}

	@Override
	public void handleEndContact(GameObject other) {
	}

	@Override
	public void onExpire() {
	}

	@Override
	public void init() {
	}
	
}
