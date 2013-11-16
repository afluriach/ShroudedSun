package com.pezventure.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.graphics.Graphics;
import com.pezventure.physics.PrimaryDirection;

public class PlayerBullet extends Bullet
{
	public static final int speed = 5;
	public static final float radius = .5f;
	public static final float mass = 0.5f;
	public static final int damage = 1;
	
	Texture texture;
	
	public PlayerBullet(Vector2 pos, PrimaryDirection dir)
	{
		super(pos, radius, "PlayerBullet", dir, speed, mass);
		
		texture = Game.inst.spriteLoader.getTexture("bullet_ec");
	}

	@Override
	public void update()
	{
		
	}

	@Override
	public void render(SpriteBatch sb) {
		Graphics.drawTexture(texture, getCenterPos(), sb);
	}

	@Override
	public void handleContact(GameObject other) {
		super.handleContact(other);
		
		if(other instanceof BlueEnemy)
		{
			((BlueEnemy)other).hit(damage);
			expire();
		}
	}

	@Override
	void onExpire() {
		
	}
	
	@Override
	public void handleEndContact(GameObject other) {
		//no-op
	}


}
