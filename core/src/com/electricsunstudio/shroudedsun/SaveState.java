package com.electricsunstudio.shroudedsun;

import java.io.Serializable;
import java.io.Writer;
import java.util.BitSet;
import java.util.HashSet;
import java.util.TreeMap;

import com.badlogic.gdx.files.FileHandle;
import com.electricsunstudio.shroudedsun.map.AreaState;

//represents persistent game state that can be loaded and stored
public class SaveState
{
	public String profileName;
	
	public String crntArea;
	public String crntSavePoint;
	
	public int gold = 0;
	
	public int crntHP = Game.STARTING_HP;
	public int crntMP = Game.STARTING_MP;
			
	public int maxHP = Game.STARTING_HP;
	public int maxMP = Game.STARTING_MP;
	
	public TreeMap<String, AreaState> areaState = new TreeMap<String, AreaState>();
	public HashSet<String> unlockedAbilities = new HashSet<String>();
}
