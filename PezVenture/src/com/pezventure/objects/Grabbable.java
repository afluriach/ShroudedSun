package com.pezventure.objects;

public interface Grabbable
{
	public boolean canGrab();
	public void onGrab();
	public void onDrop();
}
