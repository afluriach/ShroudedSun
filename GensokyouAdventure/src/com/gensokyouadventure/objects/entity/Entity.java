package com.gensokyouadventure.objects.entity;

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
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.projectile.Bullet;
import com.gensokyouadventure.physics.PrimaryDirection;

public abstract class Entity extends GameObject
{
	static final float MASS = 50.0f;
	static final float HIT_CIRCLE_RADIUS = 0.35f;
	static final float defaultFacing = 90f;
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
	
	EntityAnimation8Dir animation;
	String character;
	
	private float crntFacing = defaultFacing;
		
	/**
	 * if set, change the direction on the next cycle
	 */
	private float desiredFacingAngle = defaultFacing;
	private Vector2 desiredVel;	
	protected float speed;	
		
	public Entity(TilespaceRectMapObject to, String character, String filter, boolean stationary)
	{
		super(to);
		this.character = character;
		this.animation = Game.inst.spriteLoader.getSpriteAnimation(character, 2);
		physicsBody = Game.inst.physics.addCircleBody(to.rect.getCenter(new Vector2()), HIT_CIRCLE_RADIUS, stationary ? BodyType.StaticBody : BodyType.DynamicBody, this, MASS, false, filter);
		
		if(to.prop.containsKey("dir"))
		{
			int dir8 = 2;
			try
			{
				dir8 = PrimaryDirection.valueOf(to.prop.get("dir", String.class)).getAngle8Dir();
			}
			catch(IllegalArgumentException e)
			{
				//the given direction is not a string representing a primary direction.
				//the direction may also be an 8dir angle
				
				dir8 = to.prop.get("dir", Integer.class);
				
				if(dir8 < 0 || dir8 >= 8)
					throw new MapDataException(String.format("invalid direction %s in entity %s", to.prop.get("dir", String.class), to.name));
			}
			this.animation.setDirection(dir8);
			crntFacing = desiredFacingAngle = dir8*45f;
		}
		
		if(to.prop.containsKey("speed"))
		{
			speed = to.prop.get("speed", Float.class);
		}
		else
		{
			speed = defaultSpeed;
		}		
	}
	
	public String getCharacter()
	{
		return character;
	}
		
	public Entity(Vector2 pos, String character, int startingDir, String name, String filter, boolean stationary)
	{
		super(name);
		this.animation = Game.inst.spriteLoader.getSpriteAnimation(character, 2);
		this.character = character;
		physicsBody = Game.inst.physics.addCircleBody(pos, HIT_CIRCLE_RADIUS, stationary ? BodyType.StaticBody : BodyType.DynamicBody, this, MASS, false, filter);
	}

	
	private void updateDirection()
	{
		crntFacing = desiredFacingAngle;
		animation.setDirection(Util.getNearestDir(crntFacing));
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
			animation.stopAnimation();
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

		desiredFacingAngle = desired*45f;
	}
	
	public void setDesiredAngle(float angle)
	{
		desiredFacingAngle = angle;
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
		
		animation.accumulateDistance(getVel().len()*Game.SECONDS_PER_FRAME);
		animation.checkSound(getCenterPos());
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
	
	public float getFacingAngle()
	{
		return crntFacing;
	}
	
	public Vector2 getFacingVector()
	{
		return Util.ray(crntFacing, 1f);
	}
	
	public int getNearestDir()
	{
		return Util.getNearestDir(crntFacing);
	}
	
	public float getSpeed() {
		return speed;
	}

	
}
