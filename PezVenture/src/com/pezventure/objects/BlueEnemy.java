package com.pezventure.objects;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.PrimaryDirection;

public class BlueEnemy extends Entity implements Enemy
{
	private static final float SPEED = 1.5f;
	private static final int TOUCH_DAMAGE = 1;
	public static final int maxHP = 3;
	public static final float invulerabilityLength = 0.5f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	private static final float radarSensorRadius = 7f;
	private static final float fovAngle = 45f;
	
	private static final float minDist = 3f;
	private static final float maxDist = 5f;
	private static final float fireTimeInterval = 1f;
	//the maximum angle from the current facing direction to the target 
	//that will allow enemy to take a shot
	private static final float maxFireAngle = 15f;
	private static final float bulletSpawnDist = 0.1f;
		
	int hp = maxHP;
	float invulnerableTimeRemaining = 0;
	float fireTimeRemaining = 0f;
	
	RadarSensor radar;
	GameObject target;
	
	public BlueEnemy(TilespaceRectMapObject to) {
		
		super(to, Game.inst.spriteLoader.getSpriteAnimation("link_blue_hat",
				         PrimaryDirection.valueOf(to.prop.get("dir", String.class))));
		radar = new RadarSensor(getCenterPos(), radarSensorRadius, Player.class);
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
	void onExpire() {
		Game.inst.physics.removeBody(radar.physicsBody);
	}
	
	public void hit(int damage)
	{
		if(invulnerableTimeRemaining == 0)
		{
			hp -= damage;
			invulnerableTimeRemaining = invulerabilityLength;
			enableFlicker(invulerabilityLength, invulerabilityFlickerInterval);
		}
	}
	
	@Override
	public void update()
	{
		super.update();
		
		if(hp <= 0) expire();
		
		invulnerableTimeRemaining -= Game.SECONDS_PER_FRAME;
		if(invulnerableTimeRemaining < 0)
		{
			invulnerableTimeRemaining = 0;
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    

		if(target == null)
		{
			idle();
		}
		else
		{
			seek();
			
			fireTimeRemaining -= Game.SECONDS_PER_FRAME;
			
			if(fireTimeRemaining <= 0)
			{
				fireTimeRemaining = 0;
				shoot();
			}
		}
	}
	
	public void idle()
	{
		List<GameObject> detected = radar.getDetectedOjects(getDir().getAngle(), fovAngle);
		if(!detected.isEmpty())
		{
			target = detected.get(0);
		}
	}
	
	public void seek()
	{
		Vector2 dispToTarget = target.getCenterPos().sub(getCenterPos());
		float dist = dispToTarget.len();
		
		Vector2 desiredVel;
		
		if(dist < minDist)
		{
//			flee();
			
			desiredVel = dispToTarget.cpy().nor().scl(-1*SPEED);
		}
		else if(dist >= maxDist)
		{
//			advance();
			
			desiredVel = dispToTarget.cpy().nor().scl(SPEED);
		}
		else
		{
			//try to align with target on one axis. 
			
			float distX = target.getCenterPos().x - getCenterPos().x;
			float distY = target.getCenterPos().y - getCenterPos().y;
			
			PrimaryDirection desiredDir;
			
			if(Math.abs(distX) > Math.abs(distY))
			{
				//closer to aligning on the horizontal. (must move vertically)
				
				desiredDir = distX < 0 ? PrimaryDirection.left : PrimaryDirection.right;
				
				if(distY > 0)
				{
					desiredVel = PrimaryDirection.up.getUnitVector().scl(SPEED);
				}
				else
				{
					desiredVel = PrimaryDirection.down.getUnitVector().scl(SPEED);
				}
			}
			else
			{
				//closer to aligning on the vertical (move horizontally).
				
				desiredDir = distY < 0 ? PrimaryDirection.down : PrimaryDirection.up;
				
				if(distX > 0)
				{
					desiredVel = PrimaryDirection.right.getUnitVector().scl(SPEED);					
				}
				else
				{
					desiredVel = PrimaryDirection.left.getUnitVector().scl(SPEED);
				}
			}
			
			setDesiredDir(desiredDir);
			setDesiredVel(desiredVel);
		}
	}
	
	public void shoot()
	{
		//only fire if the angle to the target is appropriate
		float angleToTarget = target.getCenterPos().sub(getCenterPos()).angle() - getDir().getAngle();
		
		if(Math.abs(angleToTarget) < maxFireAngle)
		{
			shoot(new EnemyBullet(getCenterPos(), getDir()), bulletSpawnDist);
			fireTimeRemaining= fireTimeInterval;
		}
	}

	@Override
	public void handleEndContact(GameObject other)
	{
		//no-op
	}


}
