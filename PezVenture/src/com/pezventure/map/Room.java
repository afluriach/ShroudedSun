package com.pezventure.map;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;

public class Room
{
	String name;
	public ArrayList<Rectangle> location = new ArrayList<Rectangle>();
	
	public Rectangle getBox()
	{
		//get bounding box containing all subrooms
		
		Rectangle rect = location.get(0);
		
		for(int i=1; i < location.size(); ++i)
		{
			Rectangle subroom = location.get(i);
			
			if(subroom.x < rect.x)
			{
				rect.width += rect.x - subroom.x;
				rect.x = subroom.x;
			}
			else if(subroom.x + subroom.width > rect.x + rect.width)
			{
				rect.width = subroom.x + subroom.width - rect.x;
			}
			
			if(subroom.y < rect.y)
			{
				rect.height += rect.y - subroom.y;
				rect.y = subroom.y;				
			}
			else if(subroom.y + subroom.height > rect.y + rect.height)
			{
				rect.height = subroom.y + subroom.height - rect.y;
			}

		}
		
		return rect;
	}
}
