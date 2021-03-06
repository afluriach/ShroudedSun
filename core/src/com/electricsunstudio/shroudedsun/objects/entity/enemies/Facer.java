package com.electricsunstudio.shroudedsun.objects.entity.enemies;

import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.Util;
import com.electricsunstudio.shroudedsun.AI.AI_Util;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.entity.Entity;
import com.electricsunstudio.shroudedsun.objects.entity.characters.Player;
import com.electricsunstudio.shroudedsun.objects.projectile.EnemyBullet;
import com.electricsunstudio.shroudedsun.objects.projectile.PlayerBullet;
import com.electricsunstudio.shroudedsun.objects.projectile.StatueFireBullet;

public class Facer extends Enemy
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
	String targetName;
	
	public Facer(TilespaceRectMapObject to) {
		
		super(to, "tewi", 1);
		hp=1;
		
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
		else if(other instanceof StatueFireBullet)
		{
			hit(1);
		}
	}

	@Override
	public void onExpire() {
	}
		
	@Override
	public void update()
	{		
		float desiredSpeed = isFacingTarget() ? speed : 0f;		
		
		setDesiredVel(Util.ray(getFacingAngle(), desiredSpeed));
		super.update();
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
				
		//generalize it so it works for 4dir facer vs 8dir player. if the dot product of of the facing vectors is 
		//negative, they are facing. if < 1, they are diagonal.
		
		Vector2 facingVector = getFacingVector();
		Vector2 targetFacingVector = target.getFacingVector();
		
		boolean facing = facingVector.dot(targetFacingVector) < 0;
		boolean visible = facingVector.dot(targetDisp) > 0 && Game.inst.physics.lineOfSight(getCenterPos(), target);
		
		return (facing && visible);
			
	}

	@Override
	public void init()
	{
		target = (Entity) Game.inst.gameObjectSystem.getObjectByName(targetName);
		if(target == null) throw new RuntimeException(String.format("target, %s, not found", targetName));
	}

	@Override
	public void hit(int damage)
	{
		AI_Util.rotate(rotateClockwise, this);
	}

	@Override
	public EnemyBullet getBullet() {
		//doesn't fire
		return null;
	}
}
