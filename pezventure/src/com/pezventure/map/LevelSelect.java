package com.pezventure.map;

import com.badlogic.gdx.utils.JsonValue;

public class LevelSelect extends Area {
	
	public LevelSelect()
	{
		super(MapUtil.loadMap("maps/level_select.tmx"));
	}

	@Override
	public void init()
	{
	}

	@Override
	public void update()
	{
	}

	@Override
	public void exit()
	{
	}

	@Override
	public void load(JsonValue val)
	{
	}

	@Override
	public JsonValue save() {
		return null;
	}

}
