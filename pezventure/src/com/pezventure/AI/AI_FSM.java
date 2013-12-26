package com.pezventure.AI;

import com.pezventure.objects.GameObject;

/**
 * FSM that runs AI. This is the abstract base class. A particular class will need to be created for each agent.
 * This uses a state level approach, where current state always gives the next state. When built, this may want to 
 * be parameterized. States may have specific conditions for transitioning out of state. The FSM could be built by 
 * specifying states and the new state to be transitioned to. 
 * @author ant
 *
 */
public abstract class AI_FSM<AgentType extends GameObject>
{
	//one way to construct a FSM is to think of it like a graph. there will be a list of states, and the edges represent the transitions between states.
	//this means a state transition table with exit conditions that are checked to see when state changes will need to be performed.
	//in the AI book, the state can get a reference to the fsm and the AI agent and change the state directly. 
	
	//each state in the fsm, stored by name
//	TreeMap<String, AI_State> states;
	//for each state, a list of state transitions out of that state
	//state transitions will be checked in the order they are in the list
//	TreeMap<String, ArrayList<StateTransition>> transitions;
	
	public AI_FSM(AgentType agent)
	{
		this.agent = agent;
		crntState = getStartState();
		
	}
	
	AI_State<AgentType> crntState;
	AI_State<AgentType> prevState;
	AgentType agent;
//	ArrayList<StateTransition> crntStateTrans;
	
	public void init()
	{
		crntState = getStartState();
		
		if(crntState != null)
		{
			crntState.onEnter();
		}
	}
	
	public void update()
	{
//		AI_State nextState;
//		
//		if(crntState == null)
//		{
//			crntState = getStartState();
//			crntState.onEnter();
//			crntStateTrans = transitions.get(crntState.name);
//		}
		
		if(crntState != null)
			crntState.update();
		
//		StateTransition trans = checkTransitions();
//		
//		if(trans != null)
//		{
//			handleTransition(trans);
//		}
	}
	
	/**
	 * check if the condition has been met for any state transition.
	 * @return the state transition, if met.
	 */
//	StateTransition checkTransitions()
//	{
//		for(StateTransition trans : crntStateTrans)
//		{
//			if(trans.condition.triggered(agent, crntState))
//			{
//				return trans;
//			}
//		}
//		
//		return null;
//	}
//	
//	public void handleTransition(StateTransition trans)
//	{
//		AI_State nextState = states.get(trans.nextState);
//
//		crntState.onExit();
//		nextState.onEnter();
//		
//		crntState = nextState;
//	}
//	
//	public void addState(AI_State state)
//	{
//		states.put(state.name, state);
//	}
//	
//	public void addTransition(String fromState, StateTransition trans)
//	{
//		if(!transitions.containsKey(fromState))
//		{
//			transitions.put(fromState, new ArrayList<StateTransition>());
//		}
//		
//		transitions.get(fromState).add(trans);
//	}
	
	public void changeState(AI_State<? extends AgentType> newState)
	{
		prevState = crntState;
		crntState.onExit();
		newState.onEnter();
		crntState = (AI_State<AgentType>) newState;
	}
	
	public abstract AI_State<AgentType> getStartState();	
}