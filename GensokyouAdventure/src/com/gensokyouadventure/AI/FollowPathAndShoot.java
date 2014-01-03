package com.gensokyouadventure.AI;

import java.util.List;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.PathSegment;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.objects.EnemyBullet;
import com.gensokyouadventure.objects.Entity;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.PlayerBullet;
import com.gensokyouadventure.objects.RadarSensor;

public class FollowPathAndShoot extends FollowPath
{
	public static final float bulletSenseRadius = 3f;
	public static final float bulletSpawnDist = 0.1f;
	public static final float shootInterval = 1.5f;
	
	float timeSinceLastShot=0f;
	GameObject target;
	
	RadarSensor bulletRadar;
	
	public FollowPathAndShoot(AI_FSM<Entity> fsm, List<PathSegment> path, float speed, GameObject target)
	{
		super(fsm, path, speed, new BlueEnemyAlert(fsm, target));
		this.target = target;		
		
		bulletRadar = new RadarSensor(fsm.agent.getCenterPos(), bulletSenseRadius, PlayerBullet.class, "player_bullet_sensor");
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
		fsm.agent.shoot(new EnemyBullet(fsm.agent.getCenterPos(), angle), bulletSpawnDist);
	}
	
	public void onExit()
	{
		bulletRadar.expire();
	}

}
