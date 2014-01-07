package com.gensokyouadventure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.gensokyouadventure.graphics.Graphics;

public class Dialog
{
	public static final int trimThickess = 3;
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
	
	public Dialog(Rectangle pos, BitmapFont font, String msg)
	{
		this.pos = pos;
		this.font = font;
		this.msg = msg;
		
		lineHeight = (int) font.getLineHeight();
		lineWidth = (int) (pos.width - 2*trimThickess - 2*margin);
		
		lineStartY= (int) (pos.y+pos.height - trimThickess - margin - lineHeight/2);
		lineStartX= (int) (pos.x+trimThickess+margin);
	}
	
	public void render(SpriteBatch batch, ShapeRenderer sr)
	{
		//draw background
		sr.begin(ShapeType.Filled);		
			sr.setColor(bgColor);	
			sr.rect(pos.x + trimThickess, pos.y + trimThickess, pos.width - 2*trimThickess, pos.height - 2*trimThickess);
		sr.end();
		
		//draw trim
		sr.begin(ShapeType.Line);		
			sr.setColor(trimColor);
			sr.rectLine(pos.x, pos.y, pos.x+pos.width, pos.y, trimThickess);
			sr.rectLine(pos.x+pos.width, pos.y, pos.x+pos.width, pos.y+pos.height, trimThickess);
			sr.rectLine(pos.x+pos.width, pos.y+pos.height, pos.x, pos.y+pos.height, trimThickess);
			sr.rectLine(pos.x, pos.y+pos.height, pos.x, pos.y, trimThickess);		
		sr.end();

		batch.begin();
		batch.setColor(textColor);
		font.drawWrapped(batch, msg, lineStartX, lineStartY, lineWidth);
		batch.end();
	}
}
