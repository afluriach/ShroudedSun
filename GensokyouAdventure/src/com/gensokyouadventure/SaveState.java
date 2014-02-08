package com.gensokyouadventure;

import java.io.Serializable;
import java.io.Writer;
import java.util.BitSet;
import java.util.HashSet;
import java.util.TreeMap;

import com.badlogic.gdx.files.FileHandle;
import com.gensokyouadventure.map.AreaState;

//represents persistent game state that can be loaded and stored
public class SaveState
{
	public String profileName;
	
	public String crntArea;
	public String crntSavePoint;
	
	public TreeMap<String, AreaState> areaState = new TreeMap<String, AreaState>();
	public HashSet<String> unlockedAbilities = new HashSet<String>();
}
