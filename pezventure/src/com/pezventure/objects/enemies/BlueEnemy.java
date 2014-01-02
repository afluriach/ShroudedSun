package com.pezventure.objects.enemies;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.AI.BlueEnemyFSM;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.objects.Elemental;
import com.pezventure.objects.Enemy;
import com.pezventure.objects.EnemyBullet;
import com.pezventure.objects.Entity;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.Player;
import com.pezventure.objects.RadarSensor;
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
	
	BlueEnemyFSM fsm;
	
	public BlueEnemy(TilespaceRectMapObject to) {
		
		super(to, "sakuya", "enemy");
		
		fsm = new BlueEnemyFSM(this);
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
	public void onExpire() {
//		Game.inst.physics.removeBody(radar.physicsBody);
		//this will cause the radar's body to be removed when it expires.
		super.onExpire();
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
		if(hp <= 0) expire();
		
		invulnerableTimeRemaining -= Game.SECONDS_PER_FRAME;
		if(invulnerableTimeRemaining < 0)
		{
			invulnerableTimeRemaining = 0;
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    

		fsm.update();
		super.update();
	}	
	
	@Override
	public void handleEndContact(GameObject other)
	{
		//no-op
	}

	@Override
	public void init() {
		fsm.init();
		
	}
	
	@Override
	public float getSpeed()
	{
		return SPEED;
	}
}
