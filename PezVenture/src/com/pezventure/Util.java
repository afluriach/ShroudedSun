package com.pezventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.objects.GameObject;

public class Util
{
	public static FileHandle getInternalFile(String path)
	{
//		if(Gdx.app.getType() == ApplicationType.Desktop)
//		{
//			return Gdx.files.internal("assets/" + path);
//		}
//		else
//		{
			return Gdx.files.internal(path);
//		}
	}
	
	public static String getInternalPath(String path)
	{
		if(Gdx.app.getType() == ApplicationType.Desktop)
		{
			return "assets/" + path;
		}
		else
		{
			return path;
		}

	}
	
}
