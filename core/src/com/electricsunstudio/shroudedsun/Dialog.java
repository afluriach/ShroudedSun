package com.electricsunstudio.shroudedsun;

import java.io.IOException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.electricsunstudio.shroudedsun.graphics.Graphics;
import com.electricsunstudio.shroudedsun.graphics.Portrait;

public class Dialog
{
	static final int portraitSpacing = 20;
	static final Color focusColor = Color.WHITE;
	static final Color unfocusColor = new Color(0.5f, 0.5f, 0.5f, 0.5f);
	
	DialogFrame [] frames;
	String leftCharacter;
	String rightCharacter;
	
	public static Dialog loadFromXml(Element elem)
	{
		Dialog cons = new Dialog();
		
		int count = elem.getChildCount();
		cons.frames = new DialogFrame[count];
		
		//if the element doesn't have any attributes, the attribute map will be null
		if(elem.getAttributes() != null)
		{
			cons.leftCharacter = elem.getAttributes().containsKey("left") ? elem.getAttribute("left") : null;
			cons.rightCharacter = elem.getAttributes().containsKey("right") ? elem.getAttribute("right") : null;
		}
		
		for(int i=0;i<count; ++i)
		{
			cons.frames[i] = DialogFrame.loadFromXml(elem.getChild(i));
		}

		return cons;
	}
	
	private Dialog()
	{
		
	}
		
	public void render(SpriteBatch batch, ShapeRenderer sr, int framenum)
	{
		int width = Game.inst.screenWidth - 2*(portraitSpacing+Game.inst.GUI_EDGE_MARGIN);
		int portraitDialogWidth = width/3;
		
		Portrait leftPortrait=null, rightPortrait=null;
		DialogFrame crntFrame = frames[framenum];
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
		
		TextBox dialog = new TextBox(dialogPos, Game.inst.font, frames[framenum].msg);
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
