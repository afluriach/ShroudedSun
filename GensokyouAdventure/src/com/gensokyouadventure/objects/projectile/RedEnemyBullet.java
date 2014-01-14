package com.gensokyouadventure.objects.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.Game;

public class RedEnemyBullet extends EnemyBullet
{
	static final int DAMAGE = 3;

	public RedEnemyBullet(Vector2 pos, float angle) {
		super(pos, angle, Game.inst.spriteLoader.getTexture("bullet_aa_red"), DAMAGE);
	}

}
