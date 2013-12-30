package com.pezventure.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.graphics.Graphics;

public class EnemyBullet extends Bullet
{
	public static final float speed = 4f;
	public static final float mass = 2;
	public static final float radius = 0.5f;
	public static final int damage = 1;
	
	Texture texture;
	
	public EnemyBullet(Vector2 pos, float angle)
	{
		super(pos,radius, "EnemyBullet", angle, speed, mass, "enemy_bullet");
		texture = Game.inst.spriteLoader.getTexture("bullet_aa");
	}
	
	@Override
	public void update() {

	}

	@Override
	public void render(SpriteBatch sb)
	{
		Graphics.drawTexture(texture, getCenterPos(), sb);
	}

	@Override
	public void handleContact(GameObject other)
	{
		super.handleContact(other);
		
		if(other instanceof Player)
		{
			Player player = (Player) other;
			if(!player.shield)
				player.hit(damage);
			expire();
		}
	}
	
	@Override
	public void handleEndContact(GameObject other) {
		//no-op
	}

	@Override
	public void init() {
		
	}


}
