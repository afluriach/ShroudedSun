package com.electricsunstudio.shroudedsun.objects.entity.enemies;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.AI.TorchWalkerFSM;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.Entity;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;
import com.electricsunstudio.shroudedsun.objects.projectile.PlayerBullet;

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
		
		super(to, "komachi", "enemy", BodyType.DynamicBody);
				
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
