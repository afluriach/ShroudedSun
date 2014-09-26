package com.electricsunstudio.shroudedsun.objects.entity.characters;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.ability.Ability;
import com.electricsunstudio.shroudedsun.ability.IceBullet;
import com.electricsunstudio.shroudedsun.ability.Shield;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;

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
