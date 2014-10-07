package com.electricsunstudio.shroudedsun.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.AI.RandomWalkFSM;
import com.electricsunstudio.shroudedsun.graphics.EntityAnimation8Dir;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.interaction.Grabbable;
import com.electricsunstudio.shroudedsun.objects.entity.enemies.Enemy;
import com.electricsunstudio.shroudedsun.objects.projectile.EnemyBullet;


public class Level2Spirit extends Enemy implements Grabbable
{
    public static final int FREEZE_TIME = 9;    
    public static final float KINETIC_FRICTION_COEFF = 0.5f;

    float freeze_timer = 0;
    EntityAnimation8Dir frozen_animation;
    boolean held = false;
    
    public Level2Spirit(TilespaceRectMapObject to)
    {
		super(to, "spirit_red", 1);

		//load the frozen spirit animation so it can be displayed
		frozen_animation = Game.inst.spriteLoader.getSpriteAnimation("spirit_frozen", 2);
		fsm = new RandomWalkFSM(this);
		canDamage = false;
    }
    
    @Override
    public void handleContact(GameObject other) {
        if(other instanceof Elemental && ((Elemental)other).getElement() == Element.ice)
        {
            freeze();
        }
    }
    
    void freeze()
    {
        freeze_timer = FREEZE_TIME;
        //stop movement
		fsm.paused = true;
		//set frozen animation to current direction when freezing
		frozen_animation.setDirection(getNearestDir());
		setDesiredVel(Vector2.Zero);
    }
    
    void thaw()
    {
        //resume movement
		fsm.paused = false;
    }
    
    boolean isFrozen()
    {
        return freeze_timer > 0;
    }

    @Override
    public void update()
    {
		if(isFrozen())
		{
			freeze_timer -= Game.SECONDS_PER_FRAME;
			applyKineticFriction(KINETIC_FRICTION_COEFF);
			
			if(freeze_timer <= 0)
			{
				thaw();
			}
		}
		super.update();
    }

    @Override
    public void handleEndContact(GameObject other) {
    }
    
    @Override
    public void render(SpriteBatch sb)
    {
        //if frozen, display alternate animation frame
        if(isFrozen())
        {
            frozen_animation.render(sb, getCenterPos());
        }
        else
        {
            super.render(sb);
        }
    }

    @Override
    public boolean canGrab() {
        return isFrozen();
    }

    @Override
    public void onGrab() {
        held = true;
        //disable collision with player
        setSensor(true);
        //disable object in the air above player
        setRenderLayer(RenderLayer.aboveGround);
    }

    @Override
    public void onDrop() {
        held = false;
        setSensor(false);
        setRenderLayer(RenderLayer.floor);
    }

    @Override
    public EnemyBullet getBullet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
