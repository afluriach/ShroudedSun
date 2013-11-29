package com.pezventure;

import java.util.TreeMap;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;

//represents persistent game state that can be loaded and stored
public class SaveState implements Serializable
{
	TreeMap<String, JsonValue> areaState = new TreeMap<String, JsonValue>();
			
	@Override
	public void write(Json json)
	{
		json.toJson(this);
	}

	@Override
	public void read(Json json, JsonValue jsonData)
	{
		json.fromJson(SaveState.class, jsonData.toString());
	}
}
