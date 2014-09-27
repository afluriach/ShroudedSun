package com.electricsunstudio.shroudedsun.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.graphics.EntityAnimation8Dir;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.Element;
import com.electricsunstudio.shroudedsun.objects.Elemental;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.objects.entity.RandomWalkNPC;
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
       	//for RandomWalkNPC
        //super(to.rect.getCenter(new Vector2()), to.name, 2, "spirit_red");
        //public Enemy(TilespaceRectMapObject mo, String animation, int hp)
        super(to, "spirit_red", 1);
        
        //load the frozen spirit animation so it can be displayed
        frozen_animation = Game.inst.spriteLoader.getSpriteAnimation("spirit_frozen", 2);
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
    }
    
    void thaw()
    {
        //resume movement
    }
    
    boolean isFrozen()
    {
        return freeze_timer > 0;
    }
    
    @Override
    public void init() {
    }
    
    @Override
    public void update()
    {
        if(isFrozen())
        {
            applyKineticFriction(KINETIC_FRICTION_COEFF);
        }
        
        freeze_timer -= Game.SECONDS_PER_FRAME;
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
