package com.pezventure.objects;

import com.pezventure.Game;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.PrimaryDirection;

public class BlueEnemy extends Entity
{
	private static final float SPEED = 1.5f;
	private static final int TOUCH_DAMAGE = 1;
	public static final int maxHP = 3;
	public static final float invulerabilityLength = 0.5f;
	private static final float invulerabilityFlickerInterval = 0.1f;
	private static final float radarSensorRadius = 5f;
		
	int hp = maxHP;
	float invulnerableTimeRemaining = 0;
	
	RadarSensor radar;
	
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

		
	}
	
	public void idle()
	{
		if(!radar.getDetectedObjects().isEmpty())
		{
			
		}
	}

	@Override
	public void handleEndContact(GameObject other)
	{
		//no-op
	}


}
