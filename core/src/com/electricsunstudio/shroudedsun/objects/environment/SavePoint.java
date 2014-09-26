package com.electricsunstudio.shroudedsun.objects.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.graphics.Graphics;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.GameObjectSystem;
import com.electricsunstudio.shroudedsun.objects.RenderLayer;
import com.electricsunstudio.shroudedsun.objects.ShapeRender;

public class SavePoint extends GameObject
{
	static final float RADIUS = 0.5f;
	static final Color color = Graphics.hsva(250f, 0.4f, 0.5f, 1f);
	
	Texture texture;
	
	public SavePoint(TilespaceRectMapObject mo)
	{
		super(mo);
		
		physicsBody = Game.inst.physics.addRectBody(mo.rect, this, BodyType.StaticBody, "environmental_floor");
//		physicsBody = Game.inst.physics.addCircleBody(mo.rect.getCenter(new Vector2()), RADIUS, BodyType.StaticBody, this, 1f, false, "environmental_floor");
		renderLayer = RenderLayer.aboveGround;
		
		texture = Game.inst.spriteLoader.getTexture("tiles32");
	}

	@Override
	public void update() {
	}

	@Override
	public void render(SpriteBatch sb) {
		Graphics.drawTexture(texture, getCenterPos(), sb);
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

//	@Override
//	public void render(ShapeRenderer shapeRenderer) {
//		shapeRenderer.begin(ShapeType.Filled);
//			shapeRenderer.setColor(Color.BLUE);
//			shapeRenderer.circle(getCenterPos().x, getCenterPos().y, RADIUS);
//		shapeRenderer.end();
//
//	}

}
