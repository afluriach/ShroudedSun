package com.gensokyouadventure.map;

import com.badlogic.gdx.utils.JsonValue;

public class TorchPuzzle extends Area
{
	public TorchPuzzle()
	{
		super(MapUtil.loadMap("maps/torch_puzzle.tmx"));
	}
	
	@Override
	public void init() {

	}

	@Override
	public void update() {

	}

	@Override
	public void exit() {

	}

	@Override
	public void load(JsonValue val) {

	}

	@Override
	public JsonValue save() {
		return null;
	}

}
