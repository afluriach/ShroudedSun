package com.electricsunstudio.shroudedsun.objects.circuit;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CircuitGraph
{
	//non-incident vertices, their state is set by interaction with player
	ArrayList<Vertex> inputs = new ArrayList<Vertex>();
	//incident vertices. their state is set by input from other torches
	ArrayList<Vertex> gates = new ArrayList<Vertex>();
	
	Vertex[][] vertices;
	
	//load from map. look for LogicGateTorches.
	public CircuitGraph(int height, int width)
	{
		vertices = new Vertex[height][width];
		
		
	}
	
	//update the state of the logic gates
	public void update()
	{
		//topological sort. start with the input vertices, which all have an incidence of 0.
		//take node from list, update its state, decrement incidence of next vertex for each outgoing edge.
		
		ArrayList<Vertex>  nonIncident =  new ArrayList<Vertex>();
		nonIncident.addAll(inputs);
		
		//do not want to change or delete edges. rather, each vertex will store
		//its incident count.
		for(Vertex v : gates)
		{
			v.incidence = v.in.size();
		}
		
		while(!nonIncident.isEmpty())
		{
			Vertex crnt = nonIncident.remove(nonIncident.size()-1);
			
			//update torch if it is not an input torch
			if(crnt.torch.torchOp != null)
			{
				//look at incident edges. pack the input pins into a bitfield and eval the logic gate
				int pins = 0;
				
				for(int i=0;i<crnt.in.size(); ++i)
				{
					if(crnt.in.get(i).start.torch.isActivated())
						pins |= 1 << i;
				}
				
				crnt.torch.setLit(crnt.torch.torchOp.eval(pins, crnt.in.size()));
			}
			
			for(Edge outgoing : crnt.out)
			{
				outgoing.end.incidence--;
				
				//set the activation of each edge to represent the activation of the source torch
				outgoing.wire.setActivated(crnt.torch.isActivated());
				
				if(outgoing.end.incidence == 0)
				{
					nonIncident.add(outgoing.end);
				}
			}
		}
	}
	
	//torch
	public void addVertex(LogicGateTorch torch)
	{
		Vertex v = new Vertex();
		v.torch = torch;
		
		if(torch.torchOp == null)
		{
			inputs.add(v);
		}
		else
		{
			gates.add(v);
		}
		
		Rectangle torchPos = torch.getAABB();
		
		//occupy both the floor and ceiling for decimal coordinates,
		//i.e. mark all tiles that the rectangle partially covers.
		int x1 = (int) Math.floor(torchPos.x);
		int x2 = (int) Math.ceil(torchPos.x+torchPos.width);
		
		int y1 = (int) Math.floor(torchPos.y);
		int y2 = (int) Math.ceil(torchPos.y+torchPos.height);
		
		for(int y=y1; y <= y2; ++y)
			for(int x = x1; x <= x2; ++x)
				vertices[y][x] = v;
	}
	
	//path
	public void addEdge(Wire wire)
	{
		Edge e = new Edge();
		e.wire = wire;
		
		Vector2 startPos= wire.points.get(0);
		Vector2 endPos = wire.points.get(wire.points.size()-1);
		
		e.start = vertices[(int) startPos.y][(int) startPos.x];
		e.end = vertices[(int) endPos.y][(int) endPos.x];
		
		e.start.out.add(e);
		e.end.in.add(e);
	}
}

class Edge
{
	Wire wire;
	Vertex start, end;
}

class Vertex
{
	LogicGateTorch torch;
	ArrayList<Edge> in = new ArrayList<Edge>(), out=new ArrayList<Edge>();
	
	//used for topological sort. should be initialized to in.size() before added to incident list
	int incidence;
}