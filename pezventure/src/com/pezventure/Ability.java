package com.pezventure;

import com.badlogic.gdx.graphics.Texture;
import com.pezventure.objects.Player;

public abstract class Ability
{
	Texture icon;

	public Ability(Texture icon)
	{
		this.icon = icon;
	}
}
