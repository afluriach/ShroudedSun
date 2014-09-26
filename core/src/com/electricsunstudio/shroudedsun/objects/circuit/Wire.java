package com.electricsunstudio.shroudedsun.objects.circuit;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.graphics.Graphics;
import com.electricsunstudio.shroudedsun.map.Path;
import com.electricsunstudio.shroudedsun.map.TilespacePolylineMapObject;
import com.electricsunstudio.shroudedsun.map.TilespaceRectMapObject;
import com.electricsunstudio.shroudedsun.objects.GameObject;
import com.electricsunstudio.shroudedsun.objects.RenderLayer;
import com.electricsunstudio.shroudedsun.objects.ShapeRender;

//wire will be derived from a polyline map object and will be used to represent edges the circuit graph.
public class Wire extends GameObject implements ShapeRender
{
	private static final Color inactive = Graphics.hsva(0f, 0f, 0.2f, 1f);
	private static final Color active = Graphics.hsva(0f, 0f, 0.7f, 1f);
	private static final float wireWidth = 4f;
	
	private boolean activated;
	List<Vector2> points;
	
	public void setActivated(boolean in)
	{
		activated = in;
	}
	
	public Wire(TilespacePolylineMapObject mo)
	{
		super(mo.name);
		points = mo.points;
		renderLayer = RenderLayer.floor;
	}

	@Override
	public void update() {
	}

	@Override
	public void render(SpriteBatch sb) {		
	}

	@Override
	public void handleContact(GameObject other) {
		
	}

	@Override
	public void handleEndContact(GameObject other) {
		
	}

	@Override
	public void init() {
		
	}

	@Override
	public void render(ShapeRenderer shapeRenderer) {
		//draw line segments. draw a brighter shade if activated
		
		//shapeRenderer is set up in pixel space.
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(activated ? active : inactive);
		
		for(int i=0;i<points.size()-1; ++i)
		{
			Vector2 pt1 = points.get(i).cpy();
			Vector2 pt2 = points.get(i+1).cpy();
			
			pt1.x *= Game.PIXELS_PER_TILE;
			pt1.y *= Game.PIXELS_PER_TILE;
			pt2.x *= Game.PIXELS_PER_TILE;
			pt2.y *= Game.PIXELS_PER_TILE;
			
			shapeRenderer.rectLine(pt1, pt2, wireWidth);
		}
		
		shapeRenderer.end();
	}
}
