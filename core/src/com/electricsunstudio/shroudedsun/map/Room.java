package com.electricsunstudio.shroudedsun.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.electricsunstudio.shroudedsun.Game;

public class Room
{
	String name;
	public ArrayList<Rectangle> location = new ArrayList<Rectangle>();
	
	public Rectangle getBox()
	{
		//get bounding box containing all subrooms
		
		Rectangle rect = new Rectangle(location.get(0));
		
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
	
	//get a list of rectangles representing areas inside the bounding box that do not correspond to any room
	//i.e. blank space that is not part of any subroom, but is part of the BB encapsulating the room
	//by occluding to the bounding box and then overlaying these black rectangles, only the area of the map 
	//corresponding to the current room will be displayed
	public List<Rectangle> getBlackspace()
	{
		ArrayList<Rectangle> blanks = new ArrayList<Rectangle>();
		
		//to start, the entire BB is blank space signified by a single rectangle.
		blanks.add(getBox());
				
		//consider each subroom. splice each blank rectangle that it intersects into 
		//intersecting and non-intersecting rectangles.
		for(Rectangle subroom : location)
		{
			Iterator<Rectangle> blankIter = blanks.iterator();
			
			//the complement splices created from all the blanks that intersect this subroom. 
			ArrayList<Rectangle> blankSplices = new ArrayList<Rectangle>();
			
			while(blankIter.hasNext())
			{
				//discard the intersecting portion (this is part of a room). the non-intersecting represent blank space
				Rectangle blankRect = blankIter.next();
				
//				if(!intersects(blankRect, subroom)) continue;
				if(blankRect.overlaps(subroom))
				{
					blankIter.remove();
					blankSplices.addAll(complementSplice(blankRect, subroom));
				}
			}
			
			blanks.addAll(blankSplices);
		}
		
		return blanks;		
	}
	
	public static boolean contains (Rectangle r, Rectangle rectangle) {
		float xmin = rectangle.x;
		float xmax = xmin + rectangle.width;

		float ymin = rectangle.y;
		float ymax = ymin + rectangle.height;

		return ((xmin >= r.x && xmin <= r.x + r.width) && (xmax >= r.x && xmax <= r.x + r.width))
			&& ((ymin >= r.y && ymin <= r.y + r.height) && (ymax >= r.y && ymax <= r.y + r.height));
	}

	
	//convert the blank rectangle into a list of rectangles that together cover the same area
	//except the area of the room rectangle  
	public static List<Rectangle> complementSplice(Rectangle blank, Rectangle room)
	{		
		ArrayList<Rectangle> rects = new ArrayList<Rectangle>(4); //shouldn't create more than 4 rectangles
		
		//if left or right edge of the blank rect is beyond the edge of the room, splice the left/right side,
		//vertical span the same as the original blank
		
		if(blank.x < room.x)
		{
			//create left rectangle
			rects.add(new Rectangle(blank.x, blank.y, room.x - blank.x, blank.height));
			//Game.log(String.format("left rect: %f,%f - %f,%f", blank.x, blank.y, room.x - blank.x, blank.height));
		}
		
		if(blank.x + blank.width > room.x + room.width)
		{
			//create right rectangle
			rects.add(new Rectangle(room.x + room.width, blank.y, (blank.x+blank.width)-(room.x+room.width), blank.height));
		//	Game.log(String.format("right rect: %f,%f - %f,%f", room.x + room.width, blank.y, (blank.x+blank.width)-(room.x+room.width), blank.height));

		}
		
		if(blank.y < room.y)
		{
			//create bottom rectangle
			
			//the left edge of this rectangle will be the greater of the left edge of the room or the blank
			//the right edge will be the lesser of the right edge of the room or blank
			//this will avoid creating top/bottom rectangles that overlap with left/right, as well as handle corner cases
			//wher either a left or right rectangle will be missing
			
			float leftEdge = Math.max(blank.x,  room.x);
			float rightEdge= Math.min(blank.x+blank.width, room.x+room.width);
			
			rects.add(new Rectangle(leftEdge, blank.y, rightEdge-leftEdge, room.y - blank.y));
	//		Game.log(String.format("bottom rect: %f,%f - %f,%f", leftEdge, blank.y, rightEdge-leftEdge, room.y - blank.y));

		}
		if(blank.y + blank.height > room.y + room.height)
		{
			//create top rectangle. horizontal span the same as above
			
			float leftEdge = Math.max(blank.x,  room.x);
			float rightEdge= Math.min(blank.x+blank.width, room.x+room.width);

			rects.add(new Rectangle(leftEdge, room.y + room.height, rightEdge - leftEdge,(blank.y+blank.height)-(room.y+room.height) ));
//			Game.log(String.format("top rect: %f,%f - %f,%f", leftEdge, room.y + room.height, rightEdge - leftEdge,(blank.y+blank.height)-(room.y+room.height)));

		}
		
		return rects;
	}
}
