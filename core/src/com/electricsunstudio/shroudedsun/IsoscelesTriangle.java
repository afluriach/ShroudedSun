package com.electricsunstudio.shroudedsun;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * used to draw triangles that make up the control pad
 * @author ant
 *
 */
public class IsoscelesTriangle
{
	/**
	 * the vector going form the center point that bisects the triangle, ending at the base.
	 */
	Vector2 ray;
	/**
	 * center point representing the height of the triangle
	 */
	Vector2 center;
	
	float [] points = new float[6];
	
	public IsoscelesTriangle(Vector2 ray, Vector2 center, float baseWidth)
	{
		this.ray = ray;
		this.center = center;
		
		//move along the base to get the coords of the other
		//two points.
		//the base is normal to the ray
		
		Vector2 baseCenter = ray.cpy().add(center);
		Vector2 basePoint1;
		Vector2 basePoint2;
		
		float angle1 = ray.angle() - 90;
		float angle2 = ray.angle() + 90;
		
		if(angle1 < 0)    angle1 += 360f;
		if(angle1 >= 360) angle1 -= 360f;
		
		if(angle2 < 0) 	  angle2 += 360f;
		if(angle2 >= 360) angle2 -= 360f;
		
		basePoint1 = baseCenter.cpy().add(Util.getUnit(angle1).scl(baseWidth/2));
		basePoint2 = baseCenter.cpy().add(Util.getUnit(angle2).scl(baseWidth/2));
		
		points[0] = center.x;
		points[1] = center.y;
		
		points[2] = basePoint1.x;
		points[3] = basePoint1.y;
		
		points[4] = basePoint2.x;
		points[5] = basePoint2.y;		
	}
	
	public void render(ShapeRenderer sr)
	{
		sr.triangle(points[0], points[1], points[2], points[3], points[4], points[5]);
	}
	
	public boolean contains(Vector2 point)
	{
		Vector3 ab = new Vector3(points[2] - points[0], points[3] - points[1], 0);
		Vector3 bc = new Vector3(points[4] - points[2], points[5] - points[3], 0);
		Vector3 ca = new Vector3(points[0] - points[4], points[1] - points[5], 0);
		
		Vector3 ap = new Vector3(point.x - points[0], point.y - points[1], 0);
		Vector3 bp = new Vector3(point.x - points[2], point.y - points[3], 0);
		Vector3 cp = new Vector3(point.x - points[4], point.y - points[5], 0);
		
		float pz = ab.crs(ap).z;
		float qz = bc.crs(bp).z;
		float rz = ca.crs(cp).z;
		
		return pz <= 0 && qz <= 0 && rz <= 0 ||
			   pz >= 0 && qz >= 0 && rz >= 0;
	}
}
