package com.electricsunstudio.shroudedsun.objects;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.AI.NpcSwitchFsm;
import com.electricsunstudio.shroudedsun.AI.PredicateSwitchFSM;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.entity.NPC;
import com.electricsunstudio.shroudedsun.objects.entity.StationaryNPC;
import com.electricsunstudio.shroudedsun.predicate.Level2Cleared;
import java.util.List;

public class Gatekeeper extends NPC
{
	List<Level2Sensor> sensors;
	
    public Gatekeeper(TilespaceRectMapObject to) {
        super(to, "komachi", BodyType.KinematicBody);
    }
	
	@Override
	public void init()
	{
		fsm = new PredicateSwitchFSM(this, new Level2Cleared(), "gatekeeper1", "gatekeeper2");
		super.init();
	}
}
