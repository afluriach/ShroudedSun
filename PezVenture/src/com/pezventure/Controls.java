package com.pezventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
	public static final int buttonRadius = 40;
	public static final int buttonTrimThickness = 5;
	
	public static final int dpadLength = 240;
	public static final int dpadThickness = 60;
	public static final int dpadSectionLength = dpadLength/2 - dpadThickness/2;
	public static final int dpadSectionOffset = dpadLength/2 + dpadThickness/2;
	
	static final ButtonColorScheme colors = ButtonColorScheme.xbox;
	
	//touch
	boolean touchControls;
	boolean keyControls;
	
	//screen
	int width;
	int height;
	
	//control state
//	boolean up = false;
//	boolean down = false;
//	boolean left = false;
//	boolean right = false;
	
	int controlPad8Dir = -1;
	
	boolean a = false;
	boolean b = false;
	boolean x = false;
	boolean y = false;
	
	boolean pause = false;
	
	//GUI elements
	
	IsoscelesTriangle [] dpadTriangles = new IsoscelesTriangle[16];
	
//	Rectangle dpadUp;
//	Rectangle dpadDown;
//	Rectangle dpadLeft;
//	Rectangle dpadRight;
//	Rectangle dpadCenter;
	
	Circle buttonA;
	Circle buttonB;
	Circle buttonX;
	Circle buttonY;
	
	Circle buttonPause;
	
	Texture playerAttackIcon;
	Texture playerShieldIcon;
	
	public Controls(int width, int height, boolean touch, boolean keys)
	{
		this.width = width;
		this.height = height;
		touchControls = touch;
		keyControls = keys;
		createShapes();
		
		playerAttackIcon = Game.inst.spriteLoader.getTexture("bullet_ec");
		playerShieldIcon = Game.inst.spriteLoader.getTexture("shield32");
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
		Vector2 dpadCenter = new Vector2(margin+dpadLength/2, margin+dpadLength/2);
		float triangleBase = (float) (dpadLength/2 / (2/Math.sqrt(2) +1));
		
		for(int i=0;i<16; i++)
		{
			float angle = i*360f/16 - 11.25f;
			
			Vector2 triangleRay = Util.getUnit(angle).scl(dpadLength/2);
			dpadTriangles[i] = new IsoscelesTriangle(triangleRay, dpadCenter, triangleBase);
			
//			float angle1 = i*45f - 22.5f;
//			if(angle1 < 0) angle1 += 360;
//			
//			float angle2 = i*45f + 22.5f;
//			if(angle2 >= 360) angle2 -= 360;
//			
//			dpadTriangles[2*i] = new IsoscelesTriangle(Util.getUnit(angle1).scl(dpadLength/2), dpadCenter, triangleBase);
//			dpadTriangles[2*i+1] = new IsoscelesTriangle(Util.getUnit(angle2).scl(dpadLength/2), dpadCenter, triangleBase);
			
		}
		
//		dpadUp = new Rectangle(margin+dpadLength/2-dpadThickness/2, margin+dpadLength/2+dpadThickness/2, dpadThickness, dpadLength/2 - dpadThickness/2);
//		dpadDown = new Rectangle(margin+dpadLength/2-dpadThickness/2, margin, dpadThickness, dpadLength/2 - dpadThickness/2);
//		dpadLeft = new Rectangle(margin, margin+dpadLength/2-dpadThickness/2, dpadLength/2 - dpadThickness/2, dpadThickness);
//		dpadRight = new Rectangle(margin+dpadLength/2 + dpadThickness/2, margin+dpadLength/2-dpadThickness/2, dpadLength/2 - dpadThickness/2, dpadThickness);
//		dpadCenter = new Rectangle(margin+dpadLength/2-dpadThickness/2, margin+dpadLength/2-dpadThickness/2, dpadThickness, dpadThickness);
		
		buttonA = new Circle(width-margin-3*buttonRadius, margin+buttonRadius, buttonRadius);
		buttonB = new Circle(width-margin-buttonRadius, margin + 3*buttonRadius, buttonRadius);
		buttonX = new Circle(width-margin-5*buttonRadius, margin+3*buttonRadius, buttonRadius);
		buttonY = new Circle(width-margin-3*buttonRadius, margin+5*buttonRadius, buttonRadius);
		
		buttonPause = new Circle(width/2, margin+buttonRadius, buttonRadius);
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
		shapeRenderer.setColor(pressed ? colors.dpaddark : colors.dpadlight);
		
		shapeRenderer.rect(segment.x, segment.y, segment.width, segment.height);
	}
	
	public void drawDpadTriangle(ShapeRenderer sr, boolean activated, IsoscelesTriangle tri)
	{
		sr.setColor(activated ? colors.dpaddark : colors.dpadlight);
		
		tri.render(sr);
	}
	
	public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font)
	{
		shapeRenderer.begin(ShapeType.Filled);
		
		drawButtonOuter(shapeRenderer, colors.alight, buttonA);
		drawButtonOuter(shapeRenderer, colors.blight, buttonB);
		drawButtonOuter(shapeRenderer, colors.xlight, buttonX);
		drawButtonOuter(shapeRenderer, colors.ylight, buttonY);
		
		drawButtonInner(shapeRenderer,a, colors.alight, colors.adark, buttonA);
		drawButtonInner(shapeRenderer,b, colors.blight, colors.bdark, buttonB);
		drawButtonInner(shapeRenderer,x, colors.xlight, colors.xdark, buttonX);
		drawButtonInner(shapeRenderer,y, colors.ylight, colors.ydark, buttonY);
				
		if(touchControls)
		{
			drawButtonOuter(shapeRenderer, colors.dpadlight, buttonPause);
			drawButtonInner(shapeRenderer, pause, colors.dpadlight, colors.dpaddark, buttonPause);

			for(int i=0;i<8;++i)
			{
				drawDpadTriangle(shapeRenderer, i == controlPad8Dir, dpadTriangles[2*i]);
				drawDpadTriangle(shapeRenderer, i == controlPad8Dir, dpadTriangles[2*i+1]);
			}
		}
//		drawDpadSegment(shapeRenderer, up, dpadUp);
//		drawDpadSegment(shapeRenderer, right, dpadRight);
//		drawDpadSegment(shapeRenderer, down, dpadDown);
//		drawDpadSegment(shapeRenderer, left, dpadLeft);

//		shapeRenderer.setColor(colors.dpaddark);
//		shapeRenderer.rect(dpadCenter.x, dpadCenter.y, dpadCenter.width, dpadCenter.height);
		
		shapeRenderer.end();
		
		batch.begin();
		
		if(Game.inst.player.holdingItem != null)
			batch.setColor(1f, 1f, 1f, 0.3f);
		
		Graphics.drawTexture(playerAttackIcon, new Vector2(buttonX.x*Game.TILES_PER_PIXEL,  buttonX.y*Game.TILES_PER_PIXEL), batch);
		Graphics.drawTexture(playerShieldIcon, new Vector2(buttonB.x*Game.TILES_PER_PIXEL, buttonX.y*Game.TILES_PER_PIXEL), batch);
		
		batch.setColor(1f, 1f, 1f, 1f);
		
		batch.end();
		
		drawInteractMessage(batch, font);
		
		if(touchControls)
			drawPauseMessage(batch, font);
	}

	private void drawPauseMessage(SpriteBatch batch, BitmapFont font) {
		String pauseMsg = Game.inst.paused ? "resume" : "pause";
		
		drawTextCentered(pauseMsg, batch, font, buttonPause.x-buttonA.radius+10, buttonPause.y, 0.75f);
	}

	private void drawInteractMessage(SpriteBatch batch, BitmapFont font) {
		String interactMsg = Game.inst.player.interactMessage;
				
		drawTextCentered(interactMsg, batch, font, buttonA.x-buttonA.radius+10, buttonA.y, 1f);
	}
	
	void drawTextCentered(String msg, SpriteBatch batch, BitmapFont font, float x, float y, float scale)
	{
		font.setScale(scale);
		
		batch.begin();
		font.draw(batch, msg, x, y+font.getLineHeight()/2);
		batch.end();
		
		font.setScale(1f);
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
		
		if(buttonPause.contains(x,y)) pause = true;
		
		Vector2 point = new Vector2(x,y);
		for(int i=0;i<8;++i)
		{
			if(dpadTriangles[2*i].contains(point) || dpadTriangles[2*i+1].contains(point))
			{
				controlPad8Dir = i;
				Gdx.app.log(Game.TAG, String.format("control pad dir %d set", i));
			}
		}
		
//		if(dpadDown.contains(x,y)) down = true;
//		if(dpadLeft.contains(x,y)) left = true;
//		if(dpadUp.contains(x,y)) up = true;
//		if(dpadRight.contains(x,y)) right = true;		
	}
	
	private void checkKeyPress()
	{
		if(Gdx.input.isKeyPressed(Keys.DOWN)) a = true;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) b = true;
		if(Gdx.input.isKeyPressed(Keys.UP)) y = true;
		if(Gdx.input.isKeyPressed(Keys.LEFT)) x = true;
		
		//convert WASD to 8dir style
		boolean up = false;
		boolean down = false;
		boolean left = false;
		boolean right = false;
		Vector2 dir = new Vector2();
		
		if(Gdx.input.isKeyPressed(Keys.W)) up = true;
		if(Gdx.input.isKeyPressed(Keys.A)) left = true;
		if(Gdx.input.isKeyPressed(Keys.S)) down = true;
		if(Gdx.input.isKeyPressed(Keys.D)) right = true;
		
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) pause = true;
		
		if(up && !down)
			dir.y = 1;
		else if(down && !up)
			dir.y = -1;
		if(left && !right)
			dir.x = -1;
		else if(right && !left)
			dir.x = 1;
		
		//direction finding is not additive. this will blank any movement detected by
		//a touch event, so keys have to be checked before touch
		if(dir.len2() == 0f) controlPad8Dir = -1;
		else controlPad8Dir = (int) (dir.angle()/45f);
	}
	
	private void resetControlState()
	{
		a = false;
		b = false;
		x = false;
		y = false;
		
		pause = false;
		
		controlPad8Dir = -1;
	}
	
	public void update()
	{
		resetControlState();
		if(keyControls)
			checkKeyPress();
		if(touchControls)
			handleTouchEvents();
	}
}
