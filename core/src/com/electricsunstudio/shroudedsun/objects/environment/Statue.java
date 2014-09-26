package com.electricsunstudio.shroudedsun.objects.environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.graphics.Graphics;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.projectile.StatueFireBullet;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;

public class Statue extends GameObject
{
	static final float shotVelocity = 2.5f;
	
	//texture is drawn with statue facing left, mouth on top-left corner.
	PrimaryDirection dir;
	float fireInterval = 1.0f;
	Texture texture;
	
	
	float fireTimeRemaining = 0f;
	
	public Statue(TilespaceRectMapObject mo)
	{
		super(mo);
		
		texture = Game.inst.spriteLoader.getTexture("bronze_tortoise");
		dir = PrimaryDirection.valueOf(mo.prop.get("dir", String.class));
		if(mo.prop.containsKey("fire_interval")) fireInterval = Float.parseFloat(mo.prop.get("fire_interval", String.class));
		
		physicsBody = Game.inst.physics.addRectBody(mo.rect, this, BodyType.StaticBody, "environmental_floor");
		
	}
	@Override
	public void update() {
		if(fireTimeRemaining <= 0f)
		{
			fire();
			fireTimeRemaining += fireInterval;
		}
	}
	
	void fire()
	{
		//find position of mouth. move towards edge in facing direction, then move over unit in the clockwise direction
		//this is the bullet center pos, also add displacement for the length of the bullet
		Vector2 bulletFirePos = getCenterPos().add(dir.getUnitVector().scl(3.5f)).add(dir.rotateClockwise().getUnitVector().scl(0.75f));
		
		Game.inst.gameObjectSystem.addObject(new StatueFireBullet(bulletFirePos, dir, shotVelocity));
	}
	
	@Override
	public void render(SpriteBatch sb)
	{
		float rotation;
		switch(dir)
		{
		case left:
			rotation = 0; break;
		case down:
			rotation = 90; break;
		case right:
			rotation = 180; break;
		case up:
			rotation = 270; break;
		default:
			throw new IllegalArgumentException();
		}
		Graphics.drawTexture(texture, getCenterPos(), sb, rotation);
	}
	@Override
	public void handleContact(GameObject other) {
	}
	@Override
	public void handleEndContact(GameObject other) {
	}
	@Override
	public void init() {
	}

}
