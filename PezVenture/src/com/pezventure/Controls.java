package com.pezventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.pezventure.graphics.Graphics;

/**
 * Implements touch and button controls and keep track of control state.
 * @author ant
 * 
 */
public class Controls
{
	//constants
	public static final int maxPressEvents = 5;
	
	public static final int margin = 20;
	public static final int buttonRadius = 35;
	public static final int buttonTrimThickness = 5;
	
	public static final int dpadLength = 210;
	public static final int dpadThickness = 60;
	public static final int dpadSectionLength = dpadLength/2 - dpadThickness/2;
	public static final int dpadSectionOffset = dpadLength/2 + dpadThickness/2;
	
	public static final Color buttonAdark = Graphics.hsva(135f, .8f, .5f, 1f);
	public static final Color buttonBdark = Graphics.hsva(350f, .8f, .5f, 1f);
	public static final Color buttonXdark = Graphics.hsva(251f, .8f, .5f, 1f);
	public static final Color buttonYdark = Graphics.hsva(49f, .8f, .5f, 1f);
	
	public static final Color buttonAlight = Graphics.hsva(135f, 1f, .9f, 1f);
	public static final Color buttonBlight = Graphics.hsva(350f, 1f, .9f, 1f);
	public static final Color buttonXlight = Graphics.hsva(251f, 1f, .9f, 1f);
	public static final Color buttonYlight = Graphics.hsva(49f, 1f, .9f, 1f);
			
	public static final Color dpadDark = Graphics.hsva(251f, 0.01f, .3f, 1f);
	public static final Color dpadLight = Graphics.hsva(251f, 0.01f, .7f, 1f);
	
	//touch
	boolean touchControls;
	boolean keyControls;
	
	//screen
	int width;
	int height;
	
	//control state
	boolean up = false;
	boolean down = false;
	boolean left = false;
	boolean right = false;
	
	boolean a = false;
	boolean b = false;
	boolean x = false;
	boolean y = false;
	
	//GUI elements
	Rectangle dpadUp;
	Rectangle dpadDown;
	Rectangle dpadLeft;
	Rectangle dpadRight;
	Rectangle dpadCenter;
	
	Circle buttonA;
	Circle buttonB;
	Circle buttonX;
	Circle buttonY;
	
	public Controls(int width, int height, boolean touch, boolean keys)
	{
		this.width = width;
		this.height = height;
		touchControls = touch;
		keyControls = keys;
		createShapes();
	}
	
	/**
	 * used to change GUI layout if screen resolution changes
	 */
	public void setResolution(int width, int height)
	{
		this.width = width;
		this.height = height;
		createShapes();
	}
	
	public void createShapes()
	{
		dpadUp = new Rectangle(margin+dpadLength/2-dpadThickness/2, margin+dpadLength/2+dpadThickness/2, dpadThickness, dpadLength/2 - dpadThickness/2);
		dpadDown = new Rectangle(margin+dpadLength/2-dpadThickness/2, margin, dpadThickness, dpadLength/2 - dpadThickness/2);
		dpadLeft = new Rectangle(margin, margin+dpadLength/2-dpadThickness/2, dpadLength/2 - dpadThickness/2, dpadThickness);
		dpadRight = new Rectangle(margin+dpadLength/2 + dpadThickness/2, margin+dpadLength/2-dpadThickness/2, dpadLength/2 - dpadThickness/2, dpadThickness);
		dpadCenter = new Rectangle(margin+dpadLength/2-dpadThickness/2, margin+dpadLength/2-dpadThickness/2, dpadThickness, dpadThickness);
		
		buttonA = new Circle(width-margin-3*buttonRadius, margin+buttonRadius, buttonRadius);
		buttonB = new Circle(width-margin-buttonRadius, margin + 3*buttonRadius, buttonRadius);
		buttonX = new Circle(width-margin-5*buttonRadius, margin+3*buttonRadius, buttonRadius);
		buttonY = new Circle(width-margin-3*buttonRadius, margin+5*buttonRadius, buttonRadius);
	}
	
	//buttons will be drawn dark with a light trim, unless they
	//are pressed, then the inner color will be light as well.
	public void drawButtonInner(ShapeRenderer shapeRenderer, boolean pressed, Color light, Color dark, Circle button)
	{
		shapeRenderer.setColor(pressed ? light : dark);
		
		Graphics.drawCircle(shapeRenderer, button);
	}
	
	public void drawButtonOuter(ShapeRenderer shapeRenderer, Color color, Circle button)
	{
		shapeRenderer.setColor(color);
		shapeRenderer.circle(button.x, button.y, button.radius + buttonTrimThickness);
	}
	
	public void drawDpadSegment(ShapeRenderer shapeRenderer, boolean pressed, Rectangle segment)
	{
		shapeRenderer.setColor(pressed ? dpadDark : dpadLight);
		
		shapeRenderer.rect(segment.x, segment.y, segment.width, segment.height);
	}
	
	public void render(ShapeRenderer shapeRenderer)
	{
		shapeRenderer.begin(ShapeType.Filled);
		
		drawButtonOuter(shapeRenderer, buttonAlight, buttonA);
		drawButtonOuter(shapeRenderer, buttonBlight, buttonB);
		drawButtonOuter(shapeRenderer, buttonXlight, buttonX);
		drawButtonOuter(shapeRenderer, buttonYlight, buttonY);
		
		drawButtonInner(shapeRenderer,a, buttonAlight, buttonAdark, buttonA);
		drawButtonInner(shapeRenderer,b, buttonBlight, buttonBdark, buttonB);
		drawButtonInner(shapeRenderer,x, buttonXlight, buttonXdark, buttonX);
		drawButtonInner(shapeRenderer,y, buttonYlight, buttonYdark, buttonY);
		
		drawDpadSegment(shapeRenderer, up, dpadUp);
		drawDpadSegment(shapeRenderer, right, dpadRight);
		drawDpadSegment(shapeRenderer, down, dpadDown);
		drawDpadSegment(shapeRenderer, left, dpadLeft);

		shapeRenderer.setColor(dpadDark);
		shapeRenderer.rect(dpadCenter.x, dpadCenter.y, dpadCenter.width, dpadCenter.height);
		
		shapeRenderer.end();
	}
	
	private void handleTouchEvents()
	{
		for(int i=0; i< maxPressEvents; ++i)
		{
			if(Gdx.input.isTouched(i))
				checkTouchPress(Gdx.input.getX(i), height - Gdx.input.getY(i));
			else break;
		}
	}
	
	private void checkTouchPress(int x, int y)
	{		
		Gdx.app.log(Game.TAG, String.format("touch: %d,%d", x,y));

		if(buttonA.contains(x,y)) a = true;
		if(buttonB.contains(x,y)) b = true;
		if(buttonX.contains(x,y)) this.x = true;
		if(buttonY.contains(x,y)) this.y = true;
		
		if(dpadDown.contains(x,y)) down = true;
		if(dpadLeft.contains(x,y)) left = true;
		if(dpadUp.contains(x,y)) up = true;
		if(dpadRight.contains(x,y)) right = true;		
	}
	
	private void checkKeyPress()
	{
		if(Gdx.input.isButtonPressed(Keys.DOWN)) a = true;
		if(Gdx.input.isButtonPressed(Keys.RIGHT)) b = true;
		if(Gdx.input.isButtonPressed(Keys.UP)) y = true;
		if(Gdx.input.isButtonPressed(Keys.LEFT)) x = true;
		
		if(Gdx.input.isButtonPressed(Keys.W)) up = true;
		if(Gdx.input.isButtonPressed(Keys.A)) left = true;
		if(Gdx.input.isButtonPressed(Keys.S)) down = true;
		if(Gdx.input.isButtonPressed(Keys.D)) right = true;
		
		Gdx.app.log(Game.TAG, String.format("%b %b %b %b %b %b %b %b", a,b,x,y, up,left,down,right));
	}
	
	private void resetControlState()
	{
		a = false;
		b = false;
		x = false;
		y = false;
		
		up = false;
		down = false;
		left = false;
		right = false;
	}
	
	public void update()
	{
		resetControlState();
		if(touchControls)
			handleTouchEvents();
		if(keyControls)
			checkKeyPress();
	}
}
