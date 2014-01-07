package com.gensokyouadventure;

public class ConversationFrame
{
	boolean leftFocus;
	boolean rightFocus;
	int leftPortrait;
	int rightPortrait;
	
	String msg;

	public ConversationFrame(boolean leftFocus, boolean rightFocus,
			int leftPortrait, int rightPortrait, String msg)
	{
		this.leftFocus = leftFocus;
		this.rightFocus = rightFocus;
		this.leftPortrait = leftPortrait;
		this.rightPortrait = rightPortrait;
		this.msg = msg;
	}
	
	
}
