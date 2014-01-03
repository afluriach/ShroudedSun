package com.pezventure.objects.enemies;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.pezventure.Game;
import com.pezventure.PathSegment;
import com.pezventure.AI.AI_FSM;
import com.pezventure.AI.AI_State;
import com.pezventure.AI.TorchWalkerFSM;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.objects.Entity;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.Player;
import com.pezventure.objects.PlayerBullet;
import com.pezventure.objects.Torch;
import com.pezventure.physics.PrimaryDirection;


/**
 * a class of enemy that walks around lighting torches.
 * @author ant
 *
 */
public class TorchWalker extends Entity
{
	private static final int TOUCH_DAMAGE = 1;
	public static final float invulerabilityLength = 0.5f;
	
	float invulnerableTimeRemaining = 0;
	float speed = 1f;
	
	TorchWalkerFSM fsm;
	
	/**
	 * the object that the facer will watch.
	 */
	
	public TorchWalker(TilespaceRectMapObject to) {
		
		super(to, "komachi", "enemy", false);
				
		if(to.prop.containsKey("speed"))
		{
			speed = Float.parseFloat(to.prop.get("speed", String.class));
		}
		fsm = new TorchWalkerFSM(this);
	}

	@Override
	public void handleContact(GameObject other)
	{
		if(other instanceof Player)
		{
			((Player)other).hit(TOUCH_DAMAGE);
		}
		
		if(other instanceof PlayerBullet)
		{
			other.expire();
		}

	}

	@Override
	public void onExpire() {
	}
	
	//select a random unlit torch from the list of torches. or null if none available.
//	public Torch selectUnlitTorch()
//	{
//		ArrayList<Torch> unlit = new ArrayList<Torch>();
//		
//		for(GameObject go: torches)
//		{
//			Torch t = (Torch) go;
//			if(!t.isActivated()) unlit.add(t);
//		}
//		
//		if(unlit.size() > 0)
//		{
//			int index = Game.inst.random.nextInt(unlit.size());
//			return unlit.get(index);
//		}
//		else
//		{
//			return null;
//		}
//	}
		
	@Override
	public void update()
	{
		//The torchwalker will attempt to light all torches in a room.
		//It will select a torch, walk over to it, and light it.
		
//		if(crntTarget == null)
//		{
//			crntTarget = selectUnlitTorch();	
//		}
//		
//		if(crntTarget != null)
//		{
//			pathToPos(crntTarget.getCenterPos().add(PrimaryDirection.down.getUnitVector()));
//			
//			if(!followingPath())
//			{
//				//torch walker should have arrived. light torch
//				crntTarget.light();
//				crntTarget = null;
//			}
//		}
		
		fsm.update();
		super.update();
	}
	
	@Override
	public void handleEndContact(GameObject other)
	{
		//no-op
	}

	@Override
	public void init()
	{
		//init torch list
//		torches = Game.inst.gameObjectSystem.getObjectsByType(Torch.class);	
		fsm.init();
	}
	
	public void hit(int damage)
	{
		
	}
}
