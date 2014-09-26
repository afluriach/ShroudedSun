package com.electricsunstudio.shroudedsun.AI;

import java.util.List;

import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.PathSegment;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.Entity;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Enemy;
import com.electricsunstudio.shroudedsun.objects.projectile.EnemyBullet;

//in this state, the enemy is on alert to the presence of its target.
//the enemy will 
public class EnemyAlert extends AI_State
{
	private static final float bulletSpawnDist = 0.1f;
	private static final float minDist = 2f;
	private static final float maxDist = 6f;

	GameObject target;
	
	public EnemyAlert(AI_FSM fsm, GameObject target)
	{
		super(fsm);
		this.target = target;
	}

	@Override
	public void update()
	{
		if(target.isExpired())
			fsm.changeState(new EnemyIdle(fsm));
		
		//move around randomly, staying within a distance range of the target.
		
		List<PathSegment> path = Game.inst.getPathWithinRadius(fsm.agent.getCenterPos(), target.getCenterPos(), minDist, maxDist);
		
		fsm.changeState(new FollowPathAndShoot(fsm, path, fsm.agent.getSpeed(), target));
	}

	@Override
	public void onEnter()
	{
		System.out.println(fsm.agent.getName() + " on alert");
		if(target == null)
			target = ((EnemyIdle)fsm.prevState).targets.get(0);
	}

	@Override
	public void onExit()
	{
	}

}
