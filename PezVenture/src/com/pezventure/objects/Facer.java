package com.pezventure.objects;

import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.PrimaryDirection;

public class Facer extends Entity
{
	private static final int TOUCH_DAMAGE = 1;
	public static final float invulerabilityLength = 0.5f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	
	private static final String defaultTarget = "player";
	private static final float defaultSpeed = 1.0f;
	
	float invulnerableTimeRemaining = 0;
	
	/**
	 * the object that the facer will watch.
	 */
	Entity target;
	boolean rotateClockwise;
	float speed;
	String targetName;
	
	public Facer(TilespaceRectMapObject to) {
		
		super(to, Game.inst.spriteLoader.getSpriteAnimation("link_blue_hat",
				         PrimaryDirection.valueOf(to.prop.get("dir", String.class))));
		
		if(to.prop.containsKey("target"))
			targetName = to.prop.get("target", String.class);
		else
			targetName = defaultTarget;
				
		if(to.prop.containsKey("rotate_clockwise"))
		{
			String s = to.prop.get("rotate_clockwise", String.class);
			rotateClockwise = Boolean.parseBoolean(s);
		}
		
		if(to.prop.containsKey("speed"))
		{
			speed = Float.parseFloat(to.prop.get("speed", String.class));
		}
		else
		{
			speed = defaultSpeed;
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
			if(rotateClockwise)
				desiredDir = facing.rotateClockwise();
			else
				desiredDir = facing.rotateCounterclockwise();
			
			other.expire();
		}

	}

	@Override
	void onExpire() {
	}
		
	@Override
	public void update()
	{
		super.update();
		
		if(target == null)
		{
			target = (Entity) Game.inst.gameObjectSystem.getObjectByName(targetName);
			if(target == null) throw new RuntimeException(String.format("target, %s, not found", targetName));
		}
		
		desiredSpeed = isFacingTarget() ? speed : 0f;		
	}
	
	@Override
	public void handleEndContact(GameObject other)
	{
		//no-op
	}

	boolean isFacingTarget()
	{
		//to find if the target is in the direction of this facer's sight, check the scalar
		//projection of the displacement to the target, onto the unit vector representing its line of sight. 
		//
		//1: target is directly in front
		//positive decimal: target is in the same direction. it's angle with the facing vector is less than 90 degrees
		//0: orthogonal
		//negative decimal: more than 90 degrees away.
		//-1: directly behind
		
		Vector2 targetDisp = target.getCenterPos().sub(getCenterPos());
		
		return (facing.opposite(target.facing) && facing.getUnitVector().dot(targetDisp) > 0);
			
	}
}
