package com.gensokyouadventure.objects.entity.enemies;

import com.gensokyouadventure.AI.EnemyFSM;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.projectile.EnemyBullet;
import com.gensokyouadventure.objects.projectile.RedEnemyBullet;

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
