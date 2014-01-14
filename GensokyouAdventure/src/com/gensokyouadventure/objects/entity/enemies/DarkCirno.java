package com.gensokyouadventure.objects.entity.enemies;

import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.AI.EnemyFSM;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.projectile.EnemyBullet;

public class DarkCirno extends Enemy
{
	private static final float SPEED = 1f;
	private static final int TOUCH_DAMAGE = 1;
	public static final int maxHP = 5;
			
	public DarkCirno(TilespaceRectMapObject to) {
		
		super(to, "dark_cirno", maxHP);
		
		touchDamage = TOUCH_DAMAGE;
		speed = SPEED;
	}
	
	@Override
	public void update()
	{
		//simple AI. run into player. this assumes a room without obstacles, otherwise use FollowPath
		
		if(Game.inst.getCrntArea().getCurrentRoom(getCenterPos()) == Game.inst.getCrntArea().getCurrentRoom(Game.inst.player.getCenterPos()))
		{
			Vector2 dispToPlayer = Game.inst.player.getCenterPos().sub(getCenterPos());
			
			setDesiredAngle(dispToPlayer.angle());
			setDesiredVel(dispToPlayer.nor().scl(SPEED));
		}
				
		super.update();
	}

	@Override
	public EnemyBullet getBullet() {
		//she doesn't fire any bullets
		return null;
	}
}
