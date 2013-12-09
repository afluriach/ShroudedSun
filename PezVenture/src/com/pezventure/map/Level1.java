package com.pezventure.map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.JsonValue;
import com.pezventure.Game;
import com.pezventure.objects.Door;
import com.pezventure.objects.FloorSwitch;
import com.pezventure.objects.GameObject;

public class Level1 extends Area
{
	class SaveState
	{
		public boolean switchRoomSolved = false;
		public boolean enemyRoomSolved = false;
	}
	
	private static final int NUM_ENEMIES = 3;
	
	private Door door1;
	private Door door2;
	
	private FloorSwitch switch1;
	private FloorSwitch switch2;
	
	public boolean switchRoomSolved = false;
	public boolean enemyRoomSolved = false;
		
	List<GameObject> enemies = new ArrayList<GameObject>(3); 
	
	public Level1()
	{
		super(MapUtil.loadMap("maps/level1.tmx"));
	}

	@Override
	public void init()
	{
		door1 = (Door) Game.inst.gameObjectSystem.getObjectByName("door1");
		door2 = (Door) Game.inst.gameObjectSystem.getObjectByName("door2");
		
		switch1 = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("switch1");
		switch2 = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("switch2");
		
		for(int i=1;i<=NUM_ENEMIES; ++i)
		{
			enemies.add(Game.inst.gameObjectSystem.getObjectByName(String.format("enemy%d", i)));
		}
	}

	@Override
	public void update()
	{
		if(!switchRoomSolved && switch1.isActivated() && switch2.isActivated())
		{
			door1.unlock();
			switchRoomSolved = true;
		}
		if(!enemyRoomSolved && GameObject.allExpired(enemies))
		{
			door2.unlock();
			enemyRoomSolved = true;
		}
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
//		JsonValue val = new JsonValue(ValueType.object);
		
		SaveState st = new SaveState();
		st.enemyRoomSolved = enemyRoomSolved;
		st.switchRoomSolved = switchRoomSolved;
		
		
		return null;
	}

}
