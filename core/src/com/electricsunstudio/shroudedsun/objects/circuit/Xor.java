package com.electricsunstudio.shroudedsun.objects.circuit;

public class Xor implements LogicGate
{
	@Override
	public boolean eval(int in, int count) {
		return in == 1 || in == 2;
	}

	@Override
	public Arity getArity() {
		return Arity.two;
	}

}
