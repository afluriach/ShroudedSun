package com.electricsunstudio.shroudedsun.AI;

import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.Util;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import java.util.ArrayList;

/**
 * Entity will move around in random directions based on clearance.
 * @author toni
 */
public class RandomWalk extends AI_State
{
	Vector2 targetPos;
	float maxDistToMove;
	float minDistToMove;
	
	public RandomWalk(AI_FSM fsm, float minDist, float maxDist){
		super(fsm);
		maxDistToMove = maxDist;
		minDistToMove = minDist;
	}

	@Override
	public void update()
	{
		if(targetPos != null)
		{			
			if(fsm.agent.getCenterPos().sub(targetPos).len2() < 1)
				targetPos = null;
			else
			{
				Vector2 disp = targetPos.cpy().sub(fsm.agent.getCenterPos());
			
				fsm.agent.setDesiredVel(disp.nor().scl(fsm.agent.getSpeed()));
				fsm.agent.setDesiredDir((int) (disp.angle()/45f));
			}
		}

		if(targetPos == null)
		{
			updatePos();
		}
	}
	
		//set new target position
	public void updatePos()
	{
		float desiredDist = Game.inst.random.nextFloat()*(maxDistToMove-minDistToMove)+minDistToMove;
		
		
		//if targetPos is set, the Entity should move in the direction of it.
		//else, on update try to targetPos. Entity will stand idle when it can not find a spot to move to. 
		
		//check each of the 8 diagonal directions. use a feeler to see how far in that direction the
		//entity can move. store the best result. ignore path if less than desiredDist.
		//if the player can't move dist in any direction, don't set path.
		
		ArrayList<Integer> bestDirs = new ArrayList<Integer>(8);
		
		int bestDir = -1;
		float bestDist = 0;
		
		for(int i=0;i<8; ++i)
		{
			float dist = Game.inst.physics.distanceFeeler(fsm.agent.getCenterPos(), i*45, desiredDist, GameObject.class);
			
			if(dist > bestDist)
			{
				bestDir = i;
				bestDist = dist;
			}
			if(dist == maxDistToMove)
				bestDirs.add(i);
		}
		
		//if there are multiple directions without obstruction, choose one randomly
		if(!bestDirs.isEmpty())
		{
			bestDir = bestDirs.get(Game.inst.random.nextInt(bestDirs.size()));
		}
		
		if(bestDist < minDistToMove)
		{
			Game.log(String.format("%s: can't move more than %f. ", fsm.agent.getName(), bestDist));
			fsm.agent.setDesiredVel(Vector2.Zero);
		}
		
		else
		{
			targetPos = fsm.agent.getCenterPos().add(Util.get8DirUnit(bestDir).scl(bestDist));
			fsm.agent.setDesiredVel(Util.get8DirUnit(bestDir).scl(fsm.agent.getSpeed()));
			fsm.agent.setDesiredDir(bestDir);
		}
	}


	@Override
	public void onEnter() {
	}

	@Override
	public void onExit() {
	}	
}
