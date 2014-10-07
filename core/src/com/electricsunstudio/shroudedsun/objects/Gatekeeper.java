package com.electricsunstudio.shroudedsun.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.electricsunstudio.shroudedsun.AI.AI_FSM;
import com.electricsunstudio.shroudedsun.AI.NPC_State;
import com.electricsunstudio.shroudedsun.AI.PredicateSwitchFSM;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.event.level2.GatekeeperMove;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.entity.NPC;
import com.electricsunstudio.shroudedsun.objects.environment.Door;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;
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
	
	@Override
	public void talk()
	{
		if(((PredicateSwitchFSM)fsm).activated())
		{
			//set move action for when dialog is finished
			Game.inst.onExitDialog = new GatekeeperMove();
		}
		
		super.talk();
	}
	
	public void move()
	{
		Vector2 destination = Game.inst.getCrntArea().getMapLink("gatekeeper_dest").location.getCenter(new Vector2());
		fsm.changeState(new Moving(fsm, destination));
	}
	
	//the AI_FSM agent field has default access, meaning we cannot access it in
	//these states defined in a different package. but because these states
	//are defined within the Gatekeeper class, we can access her methods directly
	
	//after clearing the puzzle and talking to the gatekeeper, she will move out
	//of the way
	class Moving extends NPC_State
	{
		Vector2 start;
		Vector2 destination;
		float dist2;
		
		public Moving(AI_FSM fsm, Vector2 dest) {
			super(fsm);
			destination = dest;
			start = getCenterPos();
			dist2 = destination.cpy().sub(start).len2();
		}

		@Override
		public boolean canTalk() {
			return false;
		}

		@Override
		public String getDialog() {
			throw new UnsupportedOperationException("No dialog available now.");
		}
		
		float dist2Traveled()
		{
			return getCenterPos().sub(start).len2();
		}

		@Override
		public void update() {
			//entity has overshot destination or exactly arrived if displacement
			//from start (total distance traveled) is >= distance to destination
			if(dist2Traveled() >= dist2)
			{
				fsm.changeState(new Final(fsm));
			}
		}

		@Override
		public void onEnter() {
			//set velocity
			Vector2 vel = destination.cpy().sub(getCenterPos()).nor().scl(speed);
			setVel(vel);
			//set facing direction
			setDesiredAngle(vel.angle());
		}

		@Override
		public void onExit() {
			//set velocity to 0, change direction, unlock door
			setVel(Vector2.Zero);
			setDesiredDir(PrimaryDirection.left);
			Game.inst.gameObjectSystem.getObjectByName("exit", Door.class).unlock();
		}
		
	}
	
	class Final extends NPC_State
	{
		public Final(AI_FSM fsm) {
			super(fsm);
		}

		@Override
		public boolean canTalk() {
			return true;
		}

		@Override
		public String getDialog() {
			return "gatekeeper3";
		}

		@Override
		public void update() {
		}

		@Override
		public void onEnter() {
		}

		@Override
		public void onExit() {
		}
		
	}
}
