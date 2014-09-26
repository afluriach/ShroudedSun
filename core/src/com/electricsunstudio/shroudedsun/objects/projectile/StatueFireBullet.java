package com.electricsunstudio.shroudedsun.objects.projectile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.graphics.Animation;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.PlayerShieldObject;
import com.electricsunstudio.shroudedsun.objects.RadarSensor;
import com.electricsunstudio.shroudedsun.objects.RenderLayer;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;

public class StatueFireBullet extends GameObject
{
	float animationFrameInterval = 0.1f;
	Animation animation;
	
	public StatueFireBullet(Vector2 pos, PrimaryDirection dir, float speed)
	{
		super("statueBullet");
		
		Rectangle rect = new Rectangle();
		
		switch(dir)
		{
		case up:
		case down:
			rect.height = 4f;
			rect.width = 2f;
			break;
		case left:
		case right:
			rect.height = 2f;
			rect.width = 4f;
		}
		
		rect.setCenter(pos);
		
		physicsBody = Game.inst.physics.addRectBody(rect, this, BodyType.DynamicBody, "trap");
		setVel(dir.getUnitVector().scl(speed));
		animation = Game.inst.spriteLoader.loadAnimation("flame_bullet", animationFrameInterval, dir);
	}
	
	@Override
	public void render(SpriteBatch sb) {
		animation.render(getCenterPos(), sb);
	}

	@Override
	public void handleEndContact(GameObject other) {
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
		animation.advance(Game.SECONDS_PER_FRAME);
	}

	@Override
	public void handleContact(GameObject other)
	{
		if(other.getRenderLayer()== RenderLayer.groundLevel &&
		   other.getClass() != RadarSensor.class && 
		   other.getClass() != PlayerShieldObject.class)
			expire();
	}

}
