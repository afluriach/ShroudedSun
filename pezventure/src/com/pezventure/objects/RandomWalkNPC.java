package com.pezventure.objects;

import java.util.ArrayList;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.map.TilespaceRectMapObject;


public class RandomWalkNPC extends NPC
{
	static final float defaultSpeed = 1;
	static final float maxDistToMove = 4;
	static final float minDistToMove = 2;

	Vector2 targetPos;
	
	public RandomWalkNPC(TilespaceRectMapObject to, String animation) {
		super(to);

		//speed is a propety of entity and the speed key is already checked.
		//class specific default in case speed is not specified
		if(!to.prop.containsKey("speed"))
		{
			speed = defaultSpeed;
		}

	}
	
	public RandomWalkNPC(Vector2 pos, String name, int startingDir, String animation)
	{
		super(pos, name, startingDir, animation, false);
		speed = defaultSpeed;
	}
	

	/**
	 * String will be cued as an outgoing dialog. 
	 */
	Queue<String> dialogs;
	
	public void say(String msg)
	{
		dialogs.add(msg);
	}
	
	@Override
	public void handleContact(GameObject other)
	{
		if(other instanceof RandomWalkNPC)
		{
			Game.log(String.format("%s: Sorry for bumping into you, %s.", name, other.name));
		}
	}
	/**
	 * the best direction to walk in, or -1 if all paths are blocked.
	 */
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
			float dist = Game.inst.physics.distanceFeeler(getCenterPos(), i*45, desiredDist, GameObject.class);
			
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
			Game.log(String.format("%s: can't move more than %f. ", name, bestDist));
			setDesiredVel(Vector2.Zero);
		}
		
		else
		{
			targetPos = getCenterPos().add(Util.get8DirUnit(bestDir).scl(bestDist));
			setDesiredVel(Util.get8DirUnit(bestDir).scl(speed));
			setDesiredDir(bestDir);
		}
	}

	@Override
	public void onExpire()
	{
		say(String.format("%s: It's the end for me!", name));
	}

	@Override
	public void update()
	{
		if(targetPos != null)
		{			
			if(getCenterPos().sub(targetPos).len2() < 1)
				targetPos = null;
			else
			{
				Vector2 disp = targetPos.cpy().sub(getCenterPos());
			
				setDesiredVel(disp.nor().scl(speed));
				setDesiredDir((int) (disp.angle()/45f));
			}
		}

		if(targetPos == null)
		{
			updatePos();
		}		
		
		super.update();
	}

	@Override
	public void talk()
	{
		//capitalize the first letter of the name
		String properName = Character.toUpperCase(getName().charAt(0)) + getName().substring(1);
		Game.inst.setDialogMsg("Hi, my name is " + properName + ".");
	}

	@Override
	public void handleEndContact(GameObject other) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
}
