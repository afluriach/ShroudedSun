package com.gensokyouadventure.ability;

import com.badlogic.gdx.graphics.Texture;
import com.gensokyouadventure.objects.entity.characters.Player;

/**
 * Ability that can be activated
 * @author ant
 *
 */
public abstract class ToggleAbility extends Ability
{
	public ToggleAbility(Texture icon)
	{
		super(icon);
	}
	
	public abstract boolean canActivate();
	public abstract void onActivate();
	public abstract void onDeactivate();
	
	//run each frame when the ability is active
	//return boolean of whether to stay active
	public abstract boolean updateActive();
}
