package com.gensokyouadventure.objects.entity.characters;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.ability.Ability;
import com.gensokyouadventure.ability.IceBullet;
import com.gensokyouadventure.ability.Shield;
import com.gensokyouadventure.physics.PrimaryDirection;

public class Cirno extends Player
{

	public Cirno(Vector2 pos, PrimaryDirection startingDir) {
		super(pos, startingDir, "cirno");
	}
//	public Cirno()
//	{
//		entity = "cirno";
//		abilities = new ArrayList<Class <? extends Ability>>(2);
//		
//		abilities.add(IceBullet.class);
//		abilities.add(Shield.class);
//	}
}
