package com.gensokyouadventure.objects.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.graphics.Animation;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.objects.Element;
import com.gensokyouadventure.objects.Elemental;
import com.gensokyouadventure.objects.entity.enemies.BlueEnemy;
import com.gensokyouadventure.physics.PrimaryDirection;

public class PlayerIceBullet extends PlayerBullet implements Elemental
{
	public static final int speed = 5;
	public static final float radius = .5f;
	public static final float mass = 0.5f;
	public static final int damage = 1;
	
	Animation animation;
	
	public PlayerIceBullet(Vector2 pos, float angle)
	{
		super(pos, radius, "PlayerIceBullet", angle, speed, mass, damage);
		
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
