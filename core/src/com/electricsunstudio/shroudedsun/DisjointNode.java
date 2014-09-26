package com.electricsunstudio.shroudedsun;

import com.badlogic.gdx.math.Rectangle;

public class DisjointNode
{
	public boolean isRoot;
	public DisjointNode parent;
	public Rectangle rect;
	
	public DisjointNode(int x, int y)
	{
		isRoot = true;
		parent = null;
		rect = new Rectangle(x, y, 1, 1);
	}
	
	
}
