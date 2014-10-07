package com.electricsunstudio.shroudedsun.AI;

import java.util.LinkedList;
import java.util.List;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.PathSegment;
import com.electricsunstudio.shroudedsun.Util;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.RadarSensor;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Enemy;
import com.electricsunstudio.shroudedsun.objects.projectile.PlayerBullet;

public class FollowPathAndShoot extends FollowPath
{
	public static final float bulletSenseRadius = 3f;
	public static final float bulletSpawnDist = 0.1f;
	public static final float shootInterval = 1.5f;
	
	float timeSinceLastShot=0f;
	GameObject target;
	
	RadarSensor bulletRadar;
	
	public FollowPathAndShoot(AI_FSM fsm, List<PathSegment> path, float speed, GameObject target)
	{
		super(fsm, path, speed, new EnemyAlert(fsm, target));
		this.target = target;		
		

		LinkedList<Class<?>> sensing = new LinkedList<Class<?>>();
		sensing.add(PlayerBullet.class);
		bulletRadar = new RadarSensor(fsm.agent.getCenterPos(), bulletSenseRadius, sensing, "player_bullet_sensor");
	}
	
	@Override
	public void update()
	{
		super.update();
		
		bulletRadar.setPos(fsm.agent.getCenterPos());
		
		float angleToTarget = target.getCenterPos().sub(fsm.agent.getCenterPos()).angle();
		
		//overrides set direction in FollowPath. face target instead of direction of movement.
		fsm.agent.setDesiredDir(Util.getNearestDir(angleToTarget));
		
		timeSinceLastShot += Game.SECONDS_PER_FRAME;
		
		if(timeSinceLastShot >= shootInterval)
		{
			shoot(angleToTarget);
			timeSinceLastShot = 0;
		}
		
		//TODO if enemy is shot at, try to dodge. dodge will recalculate path and return to this state.
	}

	void shoot(float angle)
	{
		fsm.agent.shoot(((Enemy) fsm.agent).getBullet(), bulletSpawnDist);
	}
	
	public void onExit()
	{
		bulletRadar.expire();
	}

}
