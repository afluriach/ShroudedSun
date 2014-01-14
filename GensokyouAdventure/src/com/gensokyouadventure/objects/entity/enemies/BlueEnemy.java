package com.gensokyouadventure.objects.entity.enemies;

import com.gensokyouadventure.AI.EnemyFSM;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.projectile.BlueEnemyBullet;
import com.gensokyouadventure.objects.projectile.EnemyBullet;

public class BlueEnemy extends Enemy
{
	private static final float SPEED = 1.5f;
	private static final int TOUCH_DAMAGE = 1;
	public static final int maxHP = 3;
			
	public BlueEnemy(TilespaceRectMapObject to) {
		
		super(to, "sakuya", maxHP);
		
		fsm = new EnemyFSM(this);
		touchDamage = TOUCH_DAMAGE;
		speed = SPEED;
	}

	@Override
	public EnemyBullet getBullet() {
		return new BlueEnemyBullet(getCenterPos(), getFacingAngle());
	}
	
	
}
