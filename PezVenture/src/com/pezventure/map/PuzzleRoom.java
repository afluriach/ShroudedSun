package com.pezventure.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.objects.Door;
import com.pezventure.objects.FloorSwitch;

public class PuzzleRoom extends Area
{
	Door [] entranceDoors = new Door[5];
	Door [] exitDoors = new Door[6];
	
	Door mainExit;
	
	FloorSwitch [][] switches;	

	public PuzzleRoom()
	{
		super(MapUtil.loadMap("maps/puzzle_room.tmx"));
	}

	@Override
	public void init()
	{
//		for(int i=0;i<5; ++i)
//		{
//			entranceDoors[i] = (Door) Game.inst.gameObjectSystem.getObjectByName(String.format("door_%d_entrance", i+2));
//			entranceDoors[i].lock();
//		}
//		for(int i=0;i<6; ++i)
//		{
//			exitDoors[i] = (Door) Game.inst.gameObjectSystem.getObjectByName(String.format("door_%d_exit", i+1));
//			exitDoors[i].lock();
//		}
//		
//		mainExit = (Door) Game.inst.gameObjectSystem.getObjectByName("exit_door");
//		mainExit.lock();
//		
//		switches = new FloorSwitch[7][];
//		
//		switches[0] = new FloorSwitch[3];
//		for(int i=0;i<3; ++i)
//		{
//			switches[0][i] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName(String.format("main_switch_%d", i+1));
//		}
//		
//		switches[1] = new FloorSwitch[1];
//		switches[1][0] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("room_1_switch");
//		
//		switches[2] = new FloorSwitch[4];
//		for(int i=0;i<4; ++i)
//		{
//			switches[2][i] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName(String.format("room_2_switch_%d", i+1));
//		}
//		
//		switches[3] = new FloorSwitch[4];
//		for(int i=0;i<4; ++i)
//		{
//			switches[3][i] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName(String.format("room_3_switch_%d", i+1));
//		}
//		
//		switches[4] = new FloorSwitch[1];
//		switches[4][0] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("room_4_switch");
//
//		switches[5] = new FloorSwitch[2];
//		switches[5][0] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("room_5_left_switch");
//		switches[5][1] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("room_5_right_switch");
//
//		switches[6] = new FloorSwitch[4];
//		switches[6][0] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("room_6_switch_top");
//		switches[6][1] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("room_6_switch_left");
//		switches[6][2] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("room_6_switch_bottom");
//		switches[6][3] = (FloorSwitch) Game.inst.gameObjectSystem.getObjectByName("room_6_switch_right");

	}

	@Override
	public void update()
	{
//		for(int i=0;i<= 6; ++i)
//		{
//			checkLockLevel(i);
//		}
	}
	
	public void checkLockLevel(int level)
	{
//		System.out.println(switches[0][0]);
//		System.out.println(switches[0][1]);
//		System.out.println(switches[0][2]);
		
		//if level == 0, check open main room exit
//		if(level == 0)
//		{
//			if(Util.allActivated(switches[0]))
//				mainExit.unlock();
//			else
//				mainExit.lock();
//		}
//		else if(level >= 1 && level <= 5)
//		{
//			if(Util.allActivated(switches[level]))
//			{
//				entranceDoors[level-1].unlock();
//				exitDoors[level-1].unlock();
//			}
//			else
//			{
//				entranceDoors[level-1].lock();
//				exitDoors[level-1].lock();
//			}
//		}
//		else if(level == 6)
//		{
//			if(Util.allActivated(switches[6]))
//				exitDoors[5].unlock();
//			else
//				exitDoors[5].lock();
//		}
	}

	@Override
	public void exit() {
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
