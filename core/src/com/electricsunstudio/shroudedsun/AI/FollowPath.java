package com.electricsunstudio.shroudedsun.AI;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.PathSegment;

/**
 * Given a valid path, follow it to the end.
 * @author ant
 *
 */
public class FollowPath extends AI_State
{
	Vector2 crntWaypoint;
	List<PathSegment> path;
	float speed;
	//the next state for the entity after finishing the path
	AI_State nextState;
	
	public FollowPath(AI_FSM fsm, List<PathSegment> path, float speed, AI_State next)
	{
		super(fsm);
		this.path = path;
		this.speed = speed;
		nextState = next;
	}
	
	@Override
	public void update()
	{
		if(crntWaypoint == null) return;
		
		if(fsm.agent.getCenterPos().sub(crntWaypoint).len2() < 0.25f)
		{
			//move to the next waypoint if there is one, else path is finished
			if(!path.isEmpty())
			{
				crntWaypoint = path.get(0).end;
				path.remove(0);
			}
			else
			{
				fsm.changeState(nextState);
			}			
		}
		moveTowardsWaypoint();

		
//		Vector2 acc;
//		
//		//TODO get speed and velocity from agent
//		if(path.isEmpty())
//		{
//			//the current waypoint is the last one. arrive at it
//			acc = AI_Util.entityArrive(fsm.agent, crntWaypoint, 5f);
//		}
//		else
//		{
//			acc = AI_Util.entitySeek(fsm.agent, crntWaypoint, 2f);
//		}
	}

	@Override
	public void onEnter()
	{
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append("Entering state FollowPathToTorch.\n\n");
//		for(PathSegment segment : path)
//		{
//			sb.append(String.format("path segment from %f,%f to %f,%f.\n", segment.start.x, segment.start.y, segment.end.x, segment.end.y));
//		}
//		
//		System.out.println(sb.toString());
		
		//pop and set the first waypoint
		if(path == null || path.isEmpty())
		{
			fsm.changeState(nextState);
			return;
		}
		
		
		crntWaypoint = path.get(0).end;
		path.remove(0);
	}

	@Override
	public void onExit()
	{
		
	}
	
	/**
	 * set the agent's desired facing direction and velocity in the direction of the waypoint
	 */
	public void moveTowardsWaypoint()
	{
		Vector2 vel = crntWaypoint.cpy().sub(fsm.agent.getCenterPos()).nor().scl(speed);
		fsm.agent.setDesiredVel(vel);
		fsm.agent.setDesiredDir((int) (vel.angle()/45f));
	}

}
