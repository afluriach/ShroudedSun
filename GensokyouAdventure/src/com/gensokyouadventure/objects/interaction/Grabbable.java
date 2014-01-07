package com.gensokyouadventure.objects.interaction;

public interface Grabbable
{
	public boolean canGrab();
	public void onGrab();
	public void onDrop();
}
