package com.gensokyouadventure.objects.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.entity.enemies.Enemy;

public class PlayerBullet extends Bullet
{
	Texture texture;
	int damage;
		
	public PlayerBullet(Vector2 pos, float radius, String name, float angle, float speed, float mass, int damage)
	{
		super(pos, radius, name, angle, speed, mass, "player_bullet");
		this.damage = damage;
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
		
		if(other instanceof Enemy)
		{
			((Enemy)other).hit(damage);
			expire();
		}
	}

	@Override
	public void onExpire() {
		
	}
	
	@Override
	public void handleEndContact(GameObject other) {
	}

	@Override
	public void init() {
	}


}
