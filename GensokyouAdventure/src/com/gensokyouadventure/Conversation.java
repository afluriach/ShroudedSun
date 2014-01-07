package com.gensokyouadventure;

import java.io.IOException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.graphics.Portrait;

public class Conversation
{
	static final int portraitSpacing = 20;
	static final Color focusColor = Color.WHITE;
	static final Color unfocusColor = new Color(0.5f, 0.5f, 0.5f, 0.5f);
	
	ConversationFrame [] frames;
	String leftCharacter;
	String rightCharacter;
	
	//load a conversation from an XML file. the root element is the conversation, which includes the character names for looking up portraits
	//all XML elements within are conversation frames, with a focus (left, right, both, or neither) and a left and right portrait ID
	//(lp and rp). the content within each frame is the conversation text.
	public Conversation(String name)
	{
		XmlReader reader = new XmlReader();
		Element xmlRoot = null;
		
		try
		{
			xmlRoot = reader.parse(Util.getInternalFile("dialogs/" + name + ".xml"));
		}catch (IOException e)
		{
			Game.log("IOException while reading " + name);
		}
		
		//the first child element describes the conversation, including which characters to use
		//each subsequent child element is a conversation frame
						
		int count = xmlRoot.getChildCount();
		frames = new ConversationFrame[count];
		
		leftCharacter = xmlRoot.getAttribute("left");
		rightCharacter = xmlRoot.getAttribute("right");
		
		for(int i=0;i<count; ++i)
		{
			Element frame = xmlRoot.getChild(i);
			String leftPortrait=null, rightPortrait=null;
			int lpID, rpID;
			String focus = frame.getAttribute("focus");
			
			if(leftCharacter != null)
				leftPortrait = frame.getAttribute("lp");
			if(rightCharacter != null)
				rightPortrait = frame.getAttribute("rp");
			
			String msg = frame.getText();
			
//			System.out.println(String.format("frame %d: focus: %s, lp: %s, rp: %s, msg: %s", i, focus, leftPortrait, rightPortrait, msg));
			
			boolean leftFocus = focus.equals("left") || focus.equals("both");
			boolean rightFocus = focus.equals("right") || focus.equals("both");
			
			if(leftCharacter != null)
				lpID = Integer.parseInt(leftPortrait);
			else
				lpID = -1;
			
			if(rightCharacter != null)
				rpID = Integer.parseInt(rightPortrait);
			else
				rpID = -1;
			
			frames[i] = new ConversationFrame(leftFocus, rightFocus, lpID, rpID, msg);
		}
	}
	
	public void render(SpriteBatch batch, ShapeRenderer sr, int framenum)
	{
		int width = Game.inst.screenWidth - 2*(portraitSpacing+Game.inst.GUI_EDGE_MARGIN);
		int portraitDialogWidth = width/3;
		
		Portrait leftPortrait=null, rightPortrait=null;
		ConversationFrame crntFrame = frames[framenum];
		Texture leftPic = null, rightPic = null;
		
		if(leftCharacter != null)
		{
			leftPortrait = Game.inst.spriteLoader.getPortrait(leftCharacter);
			leftPic = leftPortrait.frames[crntFrame.leftPortrait];
		}
		if(rightCharacter != null)
		{
			rightPortrait = Game.inst.spriteLoader.getPortrait(rightCharacter);
			rightPic = rightPortrait.frames[crntFrame.rightPortrait];
		}
		
		//TODO, draw pic with transparency or tint depending on focus.
		
		batch.begin();		
			if(leftCharacter != null)
			{
				batch.setColor(crntFrame.leftFocus ? focusColor : unfocusColor);
				
				batch.draw(leftPic,
						   Game.GUI_EDGE_MARGIN,
						   Game.GUI_EDGE_MARGIN,
						   portraitDialogWidth,
						   portraitDialogWidth,
						   0,
						   0,
						   leftPic.getWidth(),
						   leftPic.getHeight(),
						   false,
						   false);
			}
			if(rightCharacter != null)
			{
				batch.setColor(crntFrame.rightFocus ? focusColor : unfocusColor);
				
				batch.draw(rightPic,
						   Game.inst.screenWidth - portraitDialogWidth-Game.GUI_EDGE_MARGIN,
						   Game.GUI_EDGE_MARGIN,
						   portraitDialogWidth,
						   portraitDialogWidth,
						   0,
						   0,
						   rightPic.getWidth(),
						   rightPic.getHeight(),
						   false,
						   false);
			}
			
		//return batch to default, no tint
		batch.setColor(Color.WHITE);
		batch.end();
		
		//compute size for dialog object;
		Rectangle dialogPos = new Rectangle(Game.GUI_EDGE_MARGIN+portraitDialogWidth+portraitSpacing,
											Game.GUI_EDGE_MARGIN,
											portraitDialogWidth,
											portraitDialogWidth);
		
		Dialog dialog = new Dialog(dialogPos, Game.inst.font, frames[framenum].msg);
		dialog.render(batch, sr);
	}
	
	public static Rectangle getDialogPos()
	{
		int width = Game.inst.screenWidth - 2*(portraitSpacing+Game.inst.GUI_EDGE_MARGIN);
		int portraitDialogWidth = width/3;
		
		return new Rectangle(Game.GUI_EDGE_MARGIN+portraitDialogWidth+portraitSpacing,
				Game.GUI_EDGE_MARGIN,
				portraitDialogWidth,
				portraitDialogWidth);
	}
}
