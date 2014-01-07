package com.gensokyouadventure.objects.environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.RenderLayer;
import com.gensokyouadventure.objects.interaction.Grabbable;
import com.gensokyouadventure.objects.projectile.Bullet;

public class Jar extends GameObject implements Grabbable
{
	public static final float radius = 0.35f;
	public static final float mass = 4f;
	public static final float kineticFriction = 2.0f;

	private Texture texture;
	private boolean isGrabbed = false;
	
	public Jar(TilespaceRectMapObject mo) {
		super(mo);
	
		physicsBody = Game.inst.physics.addCircleBody(mo.rect.getCenter(new Vector2()), radius, BodyType.DynamicBody, this, mass, false, "environmental_floor");
		
		String color = null;
		
		if(mo.prop.containsKey("color"))
			color = mo.prop.get("color", String.class);
		
		if(color == null)
			texture = Game.inst.spriteLoader.getTexture("jar");
		else
			texture = Game.inst.spriteLoader.getTexture(String.format("%s_jar", color));
		
	}

	@Override
	public void update() {
		if(!isGrabbed)
			applyKineticFriction(kineticFriction);
	}

	@Override
	public void render(SpriteBatch sb) {
		Graphics.drawTexture(texture, getCenterPos(), sb);
	}

	@Override
	public void handleContact(com.gensokyouadventure.objects.GameObject other) {
		if(other instanceof Bullet)
		{
			other.expire();
		}
	}

	@Override
	public void handleEndContact(com.gensokyouadventure.objects.GameObject other) {

	}

	@Override
	public void onExpire() {

	}

	@Override
	public boolean canGrab() {
		return true;
	}

	@Override
	public void onGrab() {
		isGrabbed = true;
		setSensor(true);
		Game.inst.gameObjectSystem.updateRenderLayer(this, RenderLayer.aboveGround);
	}

	@Override
	public void onDrop() {
		isGrabbed = false;
		setSensor(false);
		Game.inst.gameObjectSystem.updateRenderLayer(this, RenderLayer.groundLevel);
	}

	@Override
	public void init() {
	}

}
