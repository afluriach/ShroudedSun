package com.electricsunstudio.shroudedsun.objects.entity.characters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.ability.Ability;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;

//represents a playable character.
public abstract class PlayableCharacter
{
	private static HashMap<EChar, Class <? extends Player>> classMap;

	//get an instance of a subtype of Player representing the given playable character
	public static Player getCharacter(EChar ch, Vector2 pos, PrimaryDirection dir)
	{
		if(classMap == null)
		{
			classMap = new HashMap<EChar, Class<? extends Player>>();
			
			classMap.put(EChar.aya, Aya.class);
			classMap.put(EChar.cirno, Cirno.class);
			classMap.put(EChar.alice, Alice.class);
			classMap.put(EChar.marisa, Marisa.class);
			classMap.put(EChar.reimu, Reimu.class);
			classMap.put(EChar.youmu, Youmu.class);
		}
		
		Class<?> cls = classMap.get(ch);
		Constructor cons = null;
		
		try {
			cons = cls.getConstructor(Vector2.class, PrimaryDirection.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		Player playableChar = null;
		
		try {
			playableChar = (Player) cons.newInstance(pos, dir);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return playableChar;
	}

	
	//list of abilities that a particular character has. will need to check gameState in order to determine
	//which abilities are unlocked.
	public List<Class <? extends Ability>> abilities;
	//which entity represents this character
	public String entity;
}
