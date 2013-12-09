package com.pezventure.objects.enemies;

import java.util.ArrayList;

import com.pezventure.Game;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.objects.Enemy;
import com.pezventure.objects.Entity;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.Player;
import com.pezventure.objects.PlayerBullet;
import com.pezventure.objects.Torch;


/**
 * a class of enemy that walks around lighting torches.
 * @author ant
 *
 */
public class TorchWalker extends Entity implements Enemy
{
	private static final int TOUCH_DAMAGE = 1;
	public static final float invulerabilityLength = 0.5f;
	
	float invulnerableTimeRemaining = 0;
	float speed = 1f;
	
	//TODO change to Torch type after implementing torch
	ArrayList<GameObject> torches;
	Torch crntTarget;
	
	/**
	 * the object that the facer will watch.
	 */
	
	public TorchWalker(TilespaceRectMapObject to) {
		
		super(to, "utsuho", "enemy");
				
		if(to.prop.containsKey("speed"))
		{
			speed = Float.parseFloat(to.prop.get("speed", String.class));
		}
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
		
	@Override
	public void update()
	{		
		//choose a torch to light. walk over to it and light it. 
		
		if(crntTarget == null)
		{
			crntTarget = selectUnlitTorch();
		}
		
		if(crntTarget != null)
		{
			//TODO pathfinding
			
		}
				
		
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
		torches = Game.inst.gameObjectSystem.getObjectsByType(Torch.class);			
	}
}
