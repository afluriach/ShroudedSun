package com.gensokyouadventure.objects.circuit;

import com.gensokyouadventure.objects.circuit.LogicGateTorch.Arity;

public interface LogicGate
{
	enum Arity
	{
		one, two, var
	}
	
	public boolean eval(int in, int count);
	public Arity getArity();
}
