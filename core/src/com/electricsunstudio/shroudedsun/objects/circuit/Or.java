package com.electricsunstudio.shroudedsun.objects.circuit;

public class Or implements LogicGate {

	@Override
	public boolean eval(int in, int count) {
		return in != 0;
	}

	@Override
	public Arity getArity() {
		return Arity.var;
	}

}
