package com.electricsunstudio.shroudedsun.objects.circuit;

import com.electricsunstudio.shroudedsun.objects.circuit.LogicGateTorch.Arity;

public interface LogicGate
{
	enum Arity
	{
		one, two, var
	}
	
	public boolean eval(int in, int count);
	public Arity getArity();
}
