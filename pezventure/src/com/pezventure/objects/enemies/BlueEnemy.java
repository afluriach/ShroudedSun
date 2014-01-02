package com.pezventure.objects.enemies;

import com.pezventure.AI.BlueEnemyFSM;
import com.pezventure.map.TilespaceRectMapObject;

public class BlueEnemy extends Enemy
{
	private static final float SPEED = 1.5f;
	private static final int TOUCH_DAMAGE = 1;
	public static final int maxHP = 3;
			
	public BlueEnemy(TilespaceRectMapObject to) {
		
		super(to, "sakuya", maxHP);
		
		fsm = new BlueEnemyFSM(this);
		touchDamage = TOUCH_DAMAGE;
		speed = SPEED;
	}
}
