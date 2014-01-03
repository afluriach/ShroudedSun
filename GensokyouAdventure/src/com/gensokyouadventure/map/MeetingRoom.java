package com.gensokyouadventure.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.graphics.SpriteLoader;
import com.gensokyouadventure.objects.RandomWalkNPC;

public class MeetingRoom extends Area {

	public MeetingRoom() {
		super(MapUtil.loadMap("maps/meeting_room.tmx"));
	}

	@Override
	public void init()
	{
		int spriteNameRow=0;
		int spriteNameCol=0;
		
		for(int i=0;i<6;++i)
		{
			for(int j=0;j<6;++j)
			{
				Vector2 pos = new Vector2(4+i*6, 4+j*6);
				String name = SpriteLoader.spriteSheetNames[spriteNameRow][spriteNameCol++];
				
				Game.inst.gameObjectSystem.addObject(new RandomWalkNPC(pos, name, 2, name));
				
				if(spriteNameCol >= 3)
				{
					spriteNameCol -= 3;
					spriteNameRow += 1;
				}
			}
		}
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
