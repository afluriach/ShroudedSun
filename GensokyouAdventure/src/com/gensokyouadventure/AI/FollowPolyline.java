package com.gensokyouadventure.AI;

import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.map.Path;
import com.gensokyouadventure.objects.Entity;
import com.gensokyouadventure.objects.GameObject;

public class FollowPolyline extends AI_State<Entity>
{
	Path path;
	int crntTargetPoint;
	//if loop, the entity will always go forward.
	//if not, when it reaches the end it will follow the 
	//path in reverse until it reaches the first point
	boolean forwards = true;
	float speed;
	boolean loop;
	
	public FollowPolyline(AI_FSM<Entity> fsm, Path path, float speed)
	{
		super(fsm);
		this.path = path;
		this.speed = speed;
		loop = path.isLoop();
		
		crntTargetPoint = 1;
	}

	@Override
	public void update()
	{
		if(fsm.agent.getCenterPos().sub(path.getPoint(crntTargetPoint)).len2() < 0.25f)
		{
			nextPoint();
		}
		moveTowardsWaypoint();
	}
	
	void nextPoint()
	{
		if(forwards)
		{
			if(crntTargetPoint == path.length() - 1)
			{
				if(loop)
				{
					crntTargetPoint = 0;
				}
				else
				{
					//have reached the last point in the path. proceed backwards
					forwards = false;
					crntTargetPoint = path.length() - 2;
					
				}				
			}
			else
			{
				++crntTargetPoint;
			}
		}
		else
		{
			if(crntTargetPoint == 0)
			{
				//have reached the first. proceed forward again
				crntTargetPoint = 1;
				forwards = true;
			}
			else
			{
				--crntTargetPoint;
			}
		}
	}

	@Override
	public void onEnter() {
		fsm.agent.setPos(path.getPoint(0));
	}

	@Override
	public void onExit() {
	}
	
	void moveTowardsWaypoint()
	{
		Vector2 vel = path.getPoint(crntTargetPoint).cpy().sub(fsm.agent.getCenterPos()).nor().scl(speed);
		fsm.agent.setDesiredVel(vel);
		fsm.agent.setDesiredDir(Util.getNearestDir(vel.angle()));
	}


}
