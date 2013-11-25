package com.pezventure.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.pezventure.Game;
import com.pezventure.graphics.EntityAnimation;
import com.pezventure.map.TilespaceRectMapObject;
import com.pezventure.physics.Physics;
import com.pezventure.physics.PrimaryDirection;

public abstract class Entity extends GameObject
{
	//cycle through animation (show a step), every time the entity covers distance equal to one step
	static final float stepLength = 0.25f;
	static final float MASS = 50.0f;
	static final float HIT_CIRCLE_RADIUS = 0.35f;
	static final PrimaryDirection defaultFacing = PrimaryDirection.up;
	static final float acceleration = 4.5f;
	
	//entities may flicker to show temporary invulnerbility after being attacked or when about to expire. 
	boolean isFlickering = false;
	boolean showingSprite = true;
	float flickertotalTimeRemaining = 0;
	float flickerIntervalLength = 0;
	float flickerIntervalTimeRemaining = 0;
	
	float stepDistAccumulated=0;
	
	EntityAnimation animation;
	
	private PrimaryDirection crntDir = defaultFacing;
	
	/**
	 * if set, change the direction on the next cycle
	 */
	private PrimaryDirection desiredDir = defaultFacing;
	private Vector2 desiredVel;
		
	public Entity(TilespaceRectMapObject to, EntityAnimation animation)
	{
		super(to);
		this.animation = animation;
		//physicsBody = Physics.inst().addRectBody(to.rect, this, BodyType.DynamicBody, MASS, false);
		physicsBody = Game.inst.physics.addCircleBody(to.rect.getCenter(new Vector2()), HIT_CIRCLE_RADIUS, BodyType.DynamicBody, this, MASS, false);
		
		if(to.prop.containsKey("dir"))
			crntDir = desiredDir = PrimaryDirection.valueOf(to.prop.get("dir", String.class));

	}
	
	public Entity(Vector2 pos, float height, float width, float speed, EntityAnimation animation, String name)
	{
		super(name);
		this.animation = animation;
		physicsBody = Game.inst.physics.addCircleBody(pos, HIT_CIRCLE_RADIUS, BodyType.DynamicBody, this, MASS, false);
	}
	
	private void updateDirection()
	{
		if(desiredDir != null)
		{
			crntDir = desiredDir;
			animation.setDirection(crntDir);
		}
	}
	
	/**
	 * calculate and apply acceleration based on current and desired velocity
	 */
//	private void updateVelOld()
//	{
//		float crntSpeed = getVel().len();
//		float dv = acceleration*Game.SECONDS_PER_FRAME;
//		float impulse = MASS*acceleration*Game.SECONDS_PER_FRAME;
//
//		
//		if(desiredDir == crntDir)
//		{
//			
//			//to prevent overshoot or oscillation/jittering, simulate
//			//the effect of applying acceleration partially to reach the
//			//desired velocity
//			if(Math.abs(crntSpeed - desiredSpeed) < dv)
//			{
//				setVel(crntDir.getUnitVector().scl(desiredSpeed));
//				
//				if(desiredSpeed == 0f)
//				{
//					animation.resetAnimation();
//					stepDistAccumulated = 0;
//				}
//			}
//			else
//			{
//				PrimaryDirection dir = (crntSpeed < desiredSpeed) ? crntDir : crntDir.getOpposite();
//				physicsBody.applyLinearImpulse(dir.getUnitVector().scl(impulse), getCenterPos(), true);
//			}
//		}		
//		else
//		{
//			//if stopped, or nearly stopped, change direction
//			if(crntSpeed < dv)
//			{
//				setVel(Vector2.Zero);
//				animation.resetAnimation();
//				stepDistAccumulated = 0;
//				
//				crntDir = desiredDir;
//				
//				animation.setDirection(crntDir);
//			}
//			else
//			{
//				physicsBody.applyLinearImpulse(crntDir.getOpposite().getUnitVector().scl(impulse), getCenterPos(), true);
//			}
//		}
//		
//		//velocity can be changed by physics engine due to collision with 
//		//objects
////		if(desiredDir != null)
////		{
////			setVel(desiredDir.getUnitVector().scl(desiredSpeed));
////		}
////		crntDir = desiredDir;
//	}
	
	/**
	 * apply acceleration based on desired velocity
	 */
	private void updateVel()
	{
		Vector2 velDisp = desiredVel.cpy().sub(getVel());
		Vector2 dv = velDisp.cpy().nor().scl(acceleration*Game.SECONDS_PER_FRAME);
		
		
		//the calculated dv will overshoot the desired velocity. instead set it directly
		if(dv.len2() > velDisp.len2())
			setVel(desiredVel);
		else
			setVel(getVel().add(dv));
		
		if(getVel().len2() == 0f)
		{
			//stop walking animation
			animation.resetAnimation();
			stepDistAccumulated = 0;
		}
	}
	
	public void setDesiredVel(Vector2 vel)
	{
		if(vel == null)
			throw new NullPointerException();
		
		desiredVel = vel;
	}
		
	public void setDesiredDir(PrimaryDirection desired)
	{
		if(desired == null)
			throw new NullPointerException();

		desiredDir = desired;
	}
	
	public void enableFlicker(float flickerTime, float flickerInterval)
	{
		this.isFlickering= true;
		this.flickerIntervalLength = flickerInterval;
		this.flickertotalTimeRemaining = flickerTime;
		this.flickerIntervalTimeRemaining = flickerInterval;
	}
		
	public void render(SpriteBatch sb)
	{
		if(animation != null && showingSprite)
		{
			animation.render(sb, getCenterPos());
		}
	}
	
	public void update()
	{
		
		if(isFlickering)
		{
			if(flickerIntervalTimeRemaining <=0 )
			{
				showingSprite = !showingSprite;
				flickerIntervalTimeRemaining = flickerIntervalLength;
			}

			if(flickertotalTimeRemaining <=0)
			{
				isFlickering = false;
				showingSprite = true;
			}
			
			flickerIntervalTimeRemaining -= Game.SECONDS_PER_FRAME;
			flickertotalTimeRemaining -= Game.SECONDS_PER_FRAME;
			
			
		}
		
		updateDirection();
		updateVel();
		
		stepDistAccumulated += getVel().len()*Game.SECONDS_PER_FRAME;
		if(stepDistAccumulated >= stepLength)
		{
			animation.incrementFrame();
			stepDistAccumulated -= stepLength;
		}
//		Gdx.app.log(Game.TAG, String.format("step dist acc: %f, vel mag: %f", stepDistAccumulated, vel.len()));
	}

	//shoot a bullet. position the bullet such that its edge is some distance away from the edge of the entity's 
	//physics body, in the direction of its movement.
	public void shoot(Bullet b, float distance)
	{
		Vector2 dispDir = b.getDir().getUnitVector();
		float dispMag = HIT_CIRCLE_RADIUS + b.getRadius() + distance;
		Vector2 disp = dispDir.scl(dispMag);

		b.setPos(getCenterPos().add(disp));
		
		Game.inst.gameObjectSystem.addObject(b);
	}
	
	public PrimaryDirection getDir()
	{
		return crntDir;
	}
}
