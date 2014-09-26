package com.electricsunstudio.shroudedsun.objects.circuit;

import com.electricsunstudio.shroudedsun.Game;

public class And implements LogicGate {

	@Override
	public boolean eval(int bitmask, int nargs)
	{		
		return (1 << nargs) == bitmask + 1;
	}

	@Override
	public Arity getArity() {
		return Arity.var;
	}
}
