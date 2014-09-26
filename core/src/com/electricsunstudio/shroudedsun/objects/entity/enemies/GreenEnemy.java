package com.electricsunstudio.shroudedsun.objects.entity.enemies;

import com.electricsunstudio.shroudedsun.AI.EnemyFSM;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.projectile.EnemyBullet;
import com.electricsunstudio.shroudedsun.objects.projectile.GreenEnemyBullet;

public class GreenEnemy extends Enemy
{
	private static final float SPEED = 3f;
	private static final int TOUCH_DAMAGE = 1;
	public static final int maxHP = 5;
			
	public GreenEnemy(TilespaceRectMapObject to) {
		
		super(to, "yuuka", maxHP);
		
		fsm = new EnemyFSM(this);
		touchDamage = TOUCH_DAMAGE;
		speed = SPEED;
	}

	@Override
	public EnemyBullet getBullet() {
		return new GreenEnemyBullet(getCenterPos(), getFacingAngle());
	}
}
