package com.pezventure.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.graphics.Graphics;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.Physics;

public class Block extends GameObject
{
	public static final float MASS = 100.0f;
	public static final float KINETIC_FRICTION_COEFF = 0.5f;
	
	private Texture texture;
	
	private boolean moveable;
	private float pushSpeed;
	private float pushDist;
	
	private Vector2 startPos = null;
	private boolean moving = false;
	
	public Block(TilespaceRectMapObject to)
	{
		super(to);
		moveable = Boolean.parseBoolean((String) to.prop.get("moveable"));
		BodyType bodyType;
		if(moveable)
		{
			bodyType = BodyType.DynamicBody;
			pushSpeed = Float.parseFloat((String) to.prop.get("push_speed"));
			pushDist  = Float.parseFloat((String) to.prop.get("push_dist"));
		}
		else
		{
			bodyType = BodyType.StaticBody;
		}
		physicsBody = Game.inst.physics.addRectBody(to.rect, this, bodyType, MASS, false);
		texture = Game.inst.spriteLoader.getTexture("block");
	}

	@Override
	public void render(SpriteBatch sb)
	{
		Graphics.drawTexture(texture, getCenterPos(), sb);
	}

	@Override
	public void update()
	{
		applyKineticFriction(KINETIC_FRICTION_COEFF);
//		if(moving)
//		{
//			Vector2 pushDisp = getCenterPos().sub(startPos);
//			if(pushDisp.len2() >= pushDist*pushDist)
//			{
//				moving = false;
//				setVel(Vector2.Zero);
//			}
//		}

	}

	@Override
	public void handleContact(GameObject other)
	{
		if(other instanceof FloorSwitch)
			return;
		
//		pushCollider(other);
//		other.undoFrameVel();
		
//		Vector2 oldVel = other.getVel();
//		other.setVel(Vector2.Zero);

		//for now, only the player can push blocks
//		if(other instanceof Player && moveable)
//		{
//			Player player = (Player) other;
			
			//set the block and player to move at its move speed
//			setVel( oldVel.nor().scl(pushSpeed) );
//			startPos = getCenterPos();
//			moving = true;
//			other.setVel(oldVel);
//		}
		
		//if the block hits something else while moving, stop moving
//		else if(moving && other.renderLayer == RenderLayer.ground)
//		{
//			moving = false;
//			setVel(Vector2.Zero);
//		}
	}

	@Override
	void onExpire() {
		//no-op		
	}

	@Override
	public void handleEndContact(GameObject other) {
		//no-op
	}

}
