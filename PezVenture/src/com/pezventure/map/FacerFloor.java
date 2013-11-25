package com.pezventure.map;

import com.badlogic.gdx.utils.JsonValue;
import com.pezventure.Game;
import com.pezventure.objects.Door;
import com.pezventure.objects.FloorSwitch;

public class FacerFloor extends Area {
	
	public FacerFloor()
	{
		super(MapUtil.loadMap("maps/facer_floor.tmx"));
	}
	
	@Override
	public void init()
	{
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(JsonValue val) {
		// TODO Auto-generated method stub

	}

	@Override
	public JsonValue save() {
		// TODO Auto-generated method stub
		return null;
	}

}
