package com.electricsunstudio.shroudedsun.objects.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.Game;

public class BlueEnemyBullet extends EnemyBullet
{
	static final int DAMAGE = 2;

	public BlueEnemyBullet(Vector2 pos, float angle) {
		super(pos, angle, Game.inst.spriteLoader.getTexture("bullet_aa"), DAMAGE);
	}

}
