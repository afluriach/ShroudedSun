package com.pezventure.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pezventure.Game;
import com.pezventure.graphics.Animation;
import com.pezventure.graphics.Graphics;
import com.pezventure.objects.enemies.BlueEnemy;
import com.pezventure.physics.PrimaryDirection;

public class PlayerIceBullet extends Bullet implements Elemental
{
	public static final int speed = 5;
	public static final float radius = .5f;
	public static final float mass = 0.5f;
	public static final int damage = 1;
	
	Animation animation;
	
	public PlayerIceBullet(Vector2 pos, int dir)
	{
		super(pos, radius, "PlayerIceBullet", dir, speed, mass, "player_bullet");
		
		animation = Game.inst.spriteLoader.loadAnimation("cirno_bullet_aa", 0.1f, PrimaryDirection.up);
	}

	@Override
	public void update()
	{
		animation.advance(Game.SECONDS_PER_FRAME);
	}

	@Override
	public void render(SpriteBatch sb) {
		animation.render(getCenterPos(), sb);
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
	public void onExpire() {
		
	}
	
	@Override
	public void handleEndContact(GameObject other) {
	}

	@Override
	public void init() {
	}

	@Override
	public Element getElement()
	{
		return Element.ice;
	}

	@Override
	public float getElementalPower()
	{
		return 1f;
	}


}
