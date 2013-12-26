package com.pezventure.AI;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.PathSegment;
import com.pezventure.objects.Entity;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.Torch;

/**
 * the state the Torch Walker is in when it is looking for a torch to light. It will exit when it has found one. 
 * @author ant
 *
 */
public class LookingForTorch extends AI_State<Entity>
{
	public LookingForTorch(Entity agent, AI_FSM<Entity> fsm)
	{
		super("LookingForTorch", fsm);
	}

	List<GameObject> torches;
	
	@Override
	public void update()
	{
		Torch unlit = selectUnlitTorch();
		
		if(unlit != null)
		{
			//go to the tile below the target
			List<PathSegment> path = Game.inst.getPath(fsm.agent.getCenterPos(), unlit.getCenterPos().add(0, -1));
			
			if(path == null)
			{
				Gdx.app.log(Game.TAG, String.format("Found target %s at %f,%f but it is not reachable.", unlit.getName(), unlit.getCenterPos().x, unlit.getCenterPos().y));
			}
			else
			{
				Gdx.app.log(Game.TAG, "Found target");
				fsm.changeState(new FollowPath(fsm, path, fsm.agent.getSpeed(), new LightTorch(fsm, unlit)));
			}			
		}
		else
		{
			fsm.agent.setDesiredVel(Vector2.Zero);
		}
	}

	@Override
	public void onEnter()
	{
		torches = Game.inst.gameObjectSystem.getObjectsByType(Torch.class);
		//assuming that the torches in the area area static, this need only be done once, and it can be done as
		//soon as the agent enters this state.
	}

	@Override
	public void onExit()
	{
	}

	//select a random unlit torch from the list of torches. or null if none available.
	public Torch selectUnlitTorch()
	{
		ArrayList<Torch> unlit = new ArrayList<Torch>();
		
		for(GameObject go: torches)
		{
			Torch t = (Torch) go;
			if(!t.isActivated()) unlit.add(t);
		}
		
		if(unlit.size() > 0)
		{
			int index = Game.inst.random.nextInt(unlit.size());
			return unlit.get(index);
		}
		else
		{
			return null;
		}
	}

}
