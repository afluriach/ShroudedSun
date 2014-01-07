package com.gensokyouadventure.objects;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.PathSegment;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.graphics.EntityAnimation8Dir;
import com.gensokyouadventure.map.MapDataException;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.physics.PrimaryDirection;

public abstract class Entity extends GameObject
{
	static final float stepLength = 0.25f;
	static final float MASS = 50.0f;
	static final float HIT_CIRCLE_RADIUS = 0.35f;
	static final int defaultFacing = 2;
	static final float maxAcceleration = 4.5f;
	static final float defaultSpeed = 1f;
	
	public static final String[] entityNames = 
		{
			"reimu", "marisa", "cirno",
			"meiling", "patchouli", "sakuya",
			"remilia", "remilia_bat", "flandre",
			"flandre_bat", "chen", "alice",
			"shanghai", "youmu", "yuyuko",
			"ran", "yukari", "keine",
			"tewi", "reisen", "eiren",
			"kaguya", "mokou", "aya",
			"melancholy", "yuuka", "komachi",
			"yamaxanadu", "aki", "hina",
			"nitori", "sanae", "kanako",
			"suwako", "suika", "mini_suika"
		};

	
	//entities may flicker after being attacked 
	boolean isFlickering = false;
	boolean showingSprite = true;
	float flickertotalTimeRemaining = 0;
	float flickerIntervalLength = 0;
	float flickerIntervalTimeRemaining = 0;
	
	//cycle through animation (show a step), every time the entity covers distance equal to one step.
	float stepDistAccumulated=0;
	String leftFoot = "footstep_low_soft";
	String rightFoot = "footstep_high_soft";
	EntityAnimation8Dir animation;
	
	private int crntDir = defaultFacing;
		
	/**
	 * if set, change the direction on the next cycle
	 */
	private int desiredDir = defaultFacing;
	private Vector2 desiredVel;	
	protected float speed;	
		
	public Entity(TilespaceRectMapObject to, String animation, String filter, boolean stationary)
	{
		super(to);
		this.animation = Game.inst.spriteLoader.getSpriteAnimation(animation, 2);
		//physicsBody = Physics.inst().addRectBody(to.rect, this, BodyType.DynamicBody, MASS, false);
		physicsBody = Game.inst.physics.addCircleBody(to.rect.getCenter(new Vector2()), HIT_CIRCLE_RADIUS, stationary ? BodyType.StaticBody : BodyType.DynamicBody, this, MASS, false, filter);
		
		if(to.prop.containsKey("dir"))
		{
			try
			{
				crntDir = desiredDir = PrimaryDirection.valueOf(to.prop.get("dir", String.class)).getAngle8Dir();
			}
			catch(IllegalArgumentException e)
			{
				//the given dir is not a string representing a primary direction.
				//the direction may also be an 8dir angle
				
				crntDir = desiredDir = to.prop.get("dir", Integer.class);
				
				if(crntDir < 0 || crntDir >= 8)
					throw new MapDataException(String.format("invalid dir %d in entity %s", crntDir, to.name));
			}
		}
		
		if(to.prop.containsKey("speed"))
		{
			speed = to.prop.get("speed", Float.class);
		}
		else
		{
			speed = defaultSpeed;
		}
		
		this.animation.setDirection(crntDir);
	}
		
	public Entity(Vector2 pos, String animation, int startingDir, String name, String filter, boolean stationary)
	{
		super(name);
		this.animation = Game.inst.spriteLoader.getSpriteAnimation(animation, 2);
		physicsBody = Game.inst.physics.addCircleBody(pos, HIT_CIRCLE_RADIUS, stationary ? BodyType.StaticBody : BodyType.DynamicBody, this, MASS, false, filter);
	}

	
	private void updateDirection()
	{
		crntDir = desiredDir;
		animation.setDirection(crntDir);
	}
	
	
	/**
	 * apply acceleration based on desired velocity
	 */
	private void updateVel()
	{
		if(desiredVel == null) return;
		
		Vector2 velDisp = desiredVel.cpy().sub(getVel());
		Vector2 dv = velDisp.cpy().nor().scl(maxAcceleration*Game.SECONDS_PER_FRAME);
		
		
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
		
	public void setDesiredDir(int desired)
	{
		if(desired < 0 || desired >= 8)
			throw new IllegalArgumentException();

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
			
			if(animation.getFrame() == 0)
				Game.inst.soundLoader.playSound(leftFoot, getCenterPos());
			else if(animation.getFrame() == 2)
				Game.inst.soundLoader.playSound(rightFoot, getCenterPos());

		}
	}

	//shoot a bullet. position the bullet such that its edge is some distance away from the edge of the entity's 
	//physics body, in the direction of its movement.
	public void shoot(Bullet b, float distance)
	{
		Vector2 dispDir = Util.ray(b.getAngle(), 1f);
		float dispMag = HIT_CIRCLE_RADIUS + b.getRadius() + distance;
		Vector2 disp = dispDir.scl(dispMag);

		b.setPos(getCenterPos().add(disp));
		
		Game.inst.gameObjectSystem.addObject(b);
	}
	
	public int getDir()
	{
		return crntDir;
	}
	
	public float getSpeed() {
		return speed;
	}

	
}
