package com.electricsunstudio.shroudedsun.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.electricsunstudio.shroudedsun.Util;

public class Portrait
{
	public Texture[] frames;
	
	public Portrait(String name, int numPortraits)
	{
		frames = new Texture[numPortraits];
		
		for(int i=0;i<numPortraits; ++i)
		{
			frames[i] = new Texture(Util.getInternalFile(String.format("portraits/%s%d.png", name, i)));
		}
	}
}
