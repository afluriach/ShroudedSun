package com.electricsunstudio.shroudedsun.AI;

import com.electricsunstudio.shroudedsun.objects.entity.Entity;
import com.electricsunstudio.shroudedsun.predicate.Predicate;

/**
 * NPC FSM that watches for a predicate to become true
 * @author toni
 */
public class PredicateSwitchFSM extends NPC_FSM
{
	Predicate predicate;
	String inactiveDialog;
	String activeDialog;
	public PredicateSwitchFSM(Entity agent, Predicate predicate, String inactiveDialog, String activeDialog)
	{
		super(agent);
		this.predicate = predicate;
		this.inactiveDialog = inactiveDialog;
		this.activeDialog = activeDialog;
	}
	public AI_State getStartState() {
		return new PredicateInactive(this);
	}
	
	class PredicateInactive extends NPC_State
	{
		public PredicateInactive(AI_FSM fsm) {
			super(fsm);
		}
		@Override
		public boolean canTalk() {
			return true;
		}

		@Override
		public String getDialog() {
			return inactiveDialog;
		}

		@Override
		public void update() {
			if(predicate.test())
			{
				fsm.changeState(new PredicateActive(fsm));
			}
		}

		@Override
		public void onEnter() {
		}

		@Override
		public void onExit() {
		}		
	}
	
	class PredicateActive extends NPC_State
	{
		public PredicateActive(AI_FSM fsm) {
			super(fsm);
		}

		@Override
		public boolean canTalk() {
			return true;
		}

		@Override
		public String getDialog() {
			return activeDialog;
		}

		@Override
		public void update() {
			//sticky by default. to make it non-sticky to continue evaluating predicate
			//and transition back to inactive if it becomes false
		}

		@Override
		public void onEnter() {
		}

		@Override
		public void onExit() {
		}
	}
}
