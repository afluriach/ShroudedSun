package com.electricsunstudio.shroudedsun.ability;

import com.badlogic.gdx.graphics.Texture;

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
