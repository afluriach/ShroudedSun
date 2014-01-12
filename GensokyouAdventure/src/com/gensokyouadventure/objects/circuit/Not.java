package com.gensokyouadventure.objects.circuit;

public class Not implements LogicGate {

	@Override
	public boolean eval(int in, int count) {
		return in == 0;
	}

	@Override
	public Arity getArity() {
		return Arity.one;
	}

}
