package com.pezventure.map;

import com.badlogic.gdx.utils.JsonValue;

public class MansionSneakPuzzle extends Area
{
	public MansionSneakPuzzle()
	{
		super(MapUtil.loadMap("maps/mansion_sneak_puzzle.tmx"));
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
