package com.gensokyouadventure.objects.circuit;

public class Nand implements LogicGate
{
	@Override
	public boolean eval(int in, int count) {
		return !(1 << count == in + 1);
	}

	@Override
	public Arity getArity() {
		return Arity.var;
	}
}
