package com.electricsunstudio.shroudedsun;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.electricsunstudio.shroudedsun.graphics.Graphics;

public class TextBox
{
	public static final int trimThickness = 3;
	public static final int margin = 5;
	
	public static final Color textColor = Color.WHITE;
	public static final Color trimColor = Graphics.hsva(0f, 0f, 1f,1f );
	public static final Color bgColor = Graphics.hsva(180f, 0.4f, 0.2f, 0.1f);
	
	/**
	 * the footprint of the entire dialog, including trim and margin
	 */
	Rectangle pos;
	BitmapFont font;
	int lineHeight;
	int lineWidth;
	int lineStartY;
	int lineStartX;
	String msg;
	
	public TextBox(Rectangle pos, BitmapFont font, String msg)
	{
		this.pos = pos;
		this.font = font;
		this.msg = msg;
		
		lineHeight = (int) font.getLineHeight();
		lineWidth = (int) (pos.width - 2*trimThickness - 2*margin);
		
		lineStartY= (int) (pos.y+pos.height - trimThickness - margin - lineHeight/2);
		lineStartX= (int) (pos.x+trimThickness+margin);
	}
	
	public void render(SpriteBatch batch, ShapeRenderer sr)
	{
		//draw background
		sr.begin(ShapeType.Filled);		
			sr.setColor(bgColor);	
			sr.rect(pos.x + trimThickness, pos.y + trimThickness, pos.width - 2*trimThickness, pos.height - 2*trimThickness);
		sr.end();
		
		//draw trim
		sr.begin(ShapeType.Line);		
			sr.setColor(trimColor);
			sr.rectLine(pos.x, pos.y, pos.x+pos.width, pos.y, trimThickness);
			sr.rectLine(pos.x+pos.width, pos.y, pos.x+pos.width, pos.y+pos.height, trimThickness);
			sr.rectLine(pos.x+pos.width, pos.y+pos.height, pos.x, pos.y+pos.height, trimThickness);
			sr.rectLine(pos.x, pos.y+pos.height, pos.x, pos.y, trimThickness);		
		sr.end();

		batch.begin();
		font.setColor(textColor);
		font.drawWrapped(batch, msg, lineStartX, lineStartY, lineWidth);
		batch.end();
	}
}
