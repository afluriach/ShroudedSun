package com.pezventure;

import com.badlogic.gdx.graphics.Texture;
import com.pezventure.objects.Player;

/**
 * Move that can be performed reguarly
 * @author ant
 *
 */
public abstract class ActionAbility extends Ability
{
	public ActionAbility(Texture icon)
	{
		super(icon);
	}
		
	public abstract boolean canPerform();
	public abstract void perform();
}
