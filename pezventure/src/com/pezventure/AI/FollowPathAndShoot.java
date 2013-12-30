package com.pezventure.AI;

import java.util.List;

import com.pezventure.Game;
import com.pezventure.PathSegment;
import com.pezventure.Util;
import com.pezventure.objects.EnemyBullet;
import com.pezventure.objects.Entity;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.PlayerBullet;
import com.pezventure.objects.RadarSensor;

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

}
