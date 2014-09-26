package com.electricsunstudio.shroudedsun;

import com.badlogic.gdx.utils.XmlReader.Element;

public class DialogFrame
{
	boolean leftFocus;
	boolean rightFocus;
	int leftPortrait;
	int rightPortrait;
	
	String msg;

	private DialogFrame()
	{
		
	}
	
	public DialogFrame(boolean leftFocus, boolean rightFocus,
			int leftPortrait, int rightPortrait, String msg)
	{
		this.leftFocus = leftFocus;
		this.rightFocus = rightFocus;
		this.leftPortrait = leftPortrait;
		this.rightPortrait = rightPortrait;
		this.msg = msg;
	}
	
	//TODO split msg into multiple frames based on length limit
	public static DialogFrame loadFromXml(Element frame)
	{
		DialogFrame cfObj = new DialogFrame();
		
		if(frame.getAttributes().containsKey("lp"))
			cfObj.leftPortrait = frame.getIntAttribute("lp");
		else
			cfObj.leftPortrait = -1;

		if(frame.getAttributes().containsKey("rp"))
			cfObj.rightPortrait = frame.getIntAttribute("rp");
		else
			cfObj.rightPortrait = -1;
		
		cfObj.msg = frame.getText();
				
		String focus = frame.getAttribute("focus");		
		cfObj.leftFocus = focus.equals("left") || focus.equals("both");
		cfObj.rightFocus = focus.equals("right") || focus.equals("both");

		return cfObj;
	}
	
}
