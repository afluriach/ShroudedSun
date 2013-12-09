package com.pezventure;

import java.io.Serializable;
import java.io.Writer;
import java.util.BitSet;
import java.util.TreeMap;

import com.badlogic.gdx.files.FileHandle;
import com.pezventure.map.AreaState;


//represents persistent game state that can be loaded and stored
public class SaveState
{
	TreeMap<String, AreaState> areaState = new TreeMap<String, AreaState>();

}
