package com.pezventure.objects.enemies;

import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.AI.AI_Util;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.objects.Entity;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.Player;
import com.pezventure.objects.PlayerBullet;

public class Follower extends Enemy
{
	private static final int TOUCH_DAMAGE = 1;
	public static final float invulerabilityLength = 0.5f;
	
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
	
	public Follower(TilespaceRectMapObject to) {
		
		super(to, "reisen", 1);
		
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
		
	}

	@Override
	public void onExpire(){
	}
		
	@Override
	public void update()
	{
		
		float desiredSpeed = isFacingTargetsBack() ? speed : 0f;		
		
		setDesiredVel(Util.get8DirUnit(getDir()).scl(desiredSpeed));
		
		super.update();
	}
	
	@Override
	public void handleEndContact(GameObject other)
	{
		//no-op
	}

	boolean isFacingTargetsBack()
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
		
//		Gdx.app.log(Game.TAG, String.format("facing %s, target facing %s, target disp %f,%f, dot %f",
//							facing.toString(), target.facing.toString(), targetDisp.x, targetDisp.y, facing.getUnitVector().dot(targetDisp)));
		
		Vector2 facingVector = Util.get8DirUnit(getDir());
		Vector2 targetFacingVector = Util.get8DirUnit(target.getDir());
		
		boolean facingBack = facingVector.dot(targetFacingVector) > 0;
		boolean visible = facingVector.dot(targetDisp) > 0;

		
		return (facingBack && visible);
			
	}

	@Override
	public void init() {
		target = (Entity) Game.inst.gameObjectSystem.getObjectByName(targetName);
		if(target == null) throw new RuntimeException(String.format("target, %s, not found", targetName));
	}

	@Override
	public void hit(int damage)
	{
		AI_Util.rotate(rotateClockwise, this);
	}
}
