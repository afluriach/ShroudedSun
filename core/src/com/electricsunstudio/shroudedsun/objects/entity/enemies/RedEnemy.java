package com.electricsunstudio.shroudedsun.objects.entity.enemies;

import com.electricsunstudio.shroudedsun.AI.EnemyFSM;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.projectile.EnemyBullet;
import com.electricsunstudio.shroudedsun.objects.projectile.RedEnemyBullet;

public class RedEnemy extends Enemy
{
	private static final float SPEED = 1f;
	private static final int TOUCH_DAMAGE = 2;
	public static final int maxHP = 5;
			
	public RedEnemy(TilespaceRectMapObject to) {
		
		super(to, "komachi", maxHP);
		
		fsm = new EnemyFSM(this);
		touchDamage = TOUCH_DAMAGE;
		speed = SPEED;
	}

	@Override
	public EnemyBullet getBullet() {
		return new RedEnemyBullet(getCenterPos(), getFacingAngle());
	}
}
