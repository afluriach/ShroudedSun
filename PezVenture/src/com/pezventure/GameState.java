package com.pezventure;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;

//represents persistent game state that can be loaded and stored
public class GameState implements Serializable
{
	
	
	@Override
	public void write(Json json)
	{
		json.toJson(this);
	}

	@Override
	public void read(Json json, JsonValue jsonData)
	{
		json.fromJson(GameState.class, jsonData.toString());
	}
}
